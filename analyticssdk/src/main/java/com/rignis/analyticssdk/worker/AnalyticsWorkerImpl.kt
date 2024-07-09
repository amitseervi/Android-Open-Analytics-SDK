package com.rignis.analyticssdk.worker

import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DBAdapter
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.roundToLong

private const val ONE_MINUTE: Long = 60000
private const val TEN_MINUTE: Long = 10 * ONE_MINUTE
private const val LOG_TAG = "AnalyticsWorker"

internal class AnalyticsWorkerImpl(
    private val config: AnalyticsConfig,
    private val syncer: Syncer,
    private val dbAdapter: DBAdapter,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : NetworkConnectivityObserver.Callback, AnalyticsWorker {
    companion object {
        private const val EVENT_CLEANUP_EVENTS: Int = 1
        private const val EVENT_SUBMIT: Int = 2
        private const val DELETE_OLD_EVENTS: Int = 3
    }

    private val analyticsHandler: AnalyticsMessageHandler

    init {
        val handlerThread = HandlerThread("Analytics-Worker", HandlerThread.MIN_PRIORITY)
        handlerThread.start()
        analyticsHandler = AnalyticsMessageHandler(handlerThread.looper)
        networkConnectivityObserver.addCallback(this)
    }


    override fun sendEvent(name: String, params: Map<String, String>) {
        val message = analyticsHandler.obtainMessage(EVENT_SUBMIT)
        message.obj = EventMessage(name, params)
        analyticsHandler.sendMessage(message)
    }

    override fun cleanup() {
        analyticsHandler.sendMessage(analyticsHandler.obtainMessage(DELETE_OLD_EVENTS))
        analyticsHandler.sendMessage(analyticsHandler.obtainMessage(EVENT_CLEANUP_EVENTS))
    }

    private inner class AnalyticsMessageHandler(looper: Looper) : android.os.Handler(looper),
        Syncer.OnRequestCompleteCallback {
        private var mCallInProgress: Boolean = false
        private var mLastSyncTriggerTime: Long = 0L
        private var mRetryAfter: Long = 0L
        private var mFailedRetries: Long = 0L

        private fun isSyncCallInProgress(): Boolean {
            return mCallInProgress
        }

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                EVENT_SUBMIT -> {
                    Timber.tag(LOG_TAG).i("Event submit")
                    (msg.obj as? EventMessage)?.let {
                        dbAdapter.addEvent(it.name, it.params)
                    }
                    val dbSize = dbAdapter.getTotalEventCount()
                    if (networkConnectivityObserver.isNetworkAvailable) { //Check if network is available
                        Timber.tag(LOG_TAG).i("Network is Available")
                        if (!isSyncCallInProgress()) { // check if already sync call is going on
                            Timber.tag(LOG_TAG).i("No Sync is in Progress")
                            if (dbSize >= config.foregroundSyncBatchSize) {
                                Timber.tag(LOG_TAG).i("Sync Immediately")
                                removeMessages(EVENT_CLEANUP_EVENTS)
                                sendMessage(obtainMessage(EVENT_CLEANUP_EVENTS))
                            } else if (!hasMessages(EVENT_CLEANUP_EVENTS)) {
                                Timber.tag(LOG_TAG).i("No Sync Message is scheduled")
                                Timber.tag(LOG_TAG)
                                    .i("Db size = ${dbSize} and foregroundSyncBatchSize = ${config.foregroundSyncBatchSize}")
                                Timber.tag(LOG_TAG)
                                    .i("Sync Delayed ${config.foregroundSyncInterval}")
                                sendMessageDelayed(
                                    obtainMessage(EVENT_CLEANUP_EVENTS),
                                    config.foregroundSyncInterval
                                )
                            } else {
                                Timber.tag(LOG_TAG).i("Sync message already scheduled")
                                // not scheduling duplicate sync event
                            }
                        } else {
                            Timber.tag(LOG_TAG).i("Sync network call is already in progress")
                            // Do nothing we will check later when sync call complete
                            // and enqueue new sync request on success
                        }
                    } else {
                        Timber.tag(LOG_TAG).i("Network not available for syncing")
                        // if network is not available do not trigger sync request
                    }

                }

                EVENT_CLEANUP_EVENTS -> {
                    Timber.tag(LOG_TAG).i("Sync events with server")
                    mLastSyncTriggerTime = System.currentTimeMillis()
                    syncer.sync(this@AnalyticsMessageHandler)
                }

                DELETE_OLD_EVENTS -> {
                    Timber.tag(LOG_TAG).i("Delete older unsynced events")
                    dbAdapter.deleteLongPendingRequests(System.currentTimeMillis() - config.eventLifeExpiryTime)
                }

                else -> {

                }
            }
        }

        override fun onSuccess() {
            mRetryAfter = 0
            mCallInProgress = false
            mFailedRetries = 0
        }

        override fun onFail(exception: Exception) {
            mRetryAfter =
                (2.0.pow(mFailedRetries.toDouble()).roundToLong() * ONE_MINUTE).coerceAtMost(
                    TEN_MINUTE
                )
            mCallInProgress = false
            mFailedRetries++
            sendMessageDelayed(obtainMessage(EVENT_CLEANUP_EVENTS), mRetryAfter)
        }
    }

    data class EventMessage(val name: String, val params: Map<String, String>)

    override fun onNetworkAvailable() {
        if (!analyticsHandler.hasMessages(EVENT_CLEANUP_EVENTS)) {
            analyticsHandler.sendMessage(analyticsHandler.obtainMessage(EVENT_CLEANUP_EVENTS))
        }
    }
}