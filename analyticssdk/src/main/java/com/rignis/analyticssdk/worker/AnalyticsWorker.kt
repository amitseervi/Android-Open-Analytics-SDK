package com.rignis.analyticssdk.worker

import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DbAdapter
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.roundToLong

private const val ONE_MINUTE: Long = 60000
private const val TEN_MINUTE: Long = 10 * ONE_MINUTE
private const val LOG_TAG = "AnalyticsWorker"
internal class AnalyticsWorker(
    private val config: AnalyticsConfig,
    private val syncer: Syncer,
    private val dbAdapter: DbAdapter,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : NetworkConnectivityObserver.Callback {
    companion object {
        private const val EVENT_CLEANUP_EVENTS: Int = 1
        private const val EVENT_SUBMIT: Int = 2
    }

    private val analyticsHandler: AnalyticsMessageHandler

    init {
        val handlerThread = HandlerThread("Analytics-Worker", HandlerThread.MIN_PRIORITY)
        handlerThread.start()
        analyticsHandler = AnalyticsMessageHandler(handlerThread.looper)
        networkConnectivityObserver.addCallback(this)
    }


    fun sendEvent(name: String, params: Map<String, String>) {
        val message = analyticsHandler.obtainMessage(EVENT_SUBMIT)
        message.obj = EventMessage(name, params)
        analyticsHandler.sendMessage(message)
    }

    fun cleanup() {
        analyticsHandler.sendMessage(analyticsHandler.obtainMessage(EVENT_CLEANUP_EVENTS))
    }

    private inner class AnalyticsMessageHandler(looper: Looper) : android.os.Handler(looper),
        Syncer.OnRequestCompleteCallback {
        private var mCallInProgress: Boolean = false
        private var mLastSyncTriggerTime: Long = 0L
        private var mRetryAfter: Long = 0L
        private var mFailedRetries: Long = 0L


        private fun checkNetwork(): Boolean {
            return networkConnectivityObserver.isNetworkAvailable()
        }

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
                    if (checkNetwork()) { //Check if network is available
                        if (!isSyncCallInProgress()) { // check if already sync call is going on
                            if (!hasMessages(EVENT_CLEANUP_EVENTS)) {
                                if (dbSize > config.foregroundSyncBatchSize) {
                                    // if we have reached threashold where
                                    // we need to trigger force sync request
                                    sendMessage(obtainMessage(EVENT_CLEANUP_EVENTS))
                                } else {
                                    sendMessageDelayed(
                                        obtainMessage(EVENT_CLEANUP_EVENTS),
                                        config.foregroundSyncInterval
                                    )
                                }
                            } else {
                                // not scheduling duplicate sync event
                            }
                        } else {
                            // Do nothing we will check later when sync call complete
                            // and enqueue new sync request on success
                        }
                    } else {
                        // if network is not available do not trigger sync request
                    }

                }

                EVENT_CLEANUP_EVENTS -> {
                    Timber.tag(LOG_TAG).i("Sync events with server")
                    mLastSyncTriggerTime = System.currentTimeMillis()
                    syncer.sync(this@AnalyticsMessageHandler)
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