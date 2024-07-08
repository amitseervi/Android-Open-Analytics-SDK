package com.rignis.analyticssdk.worker

import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DbAdapter
import timber.log.Timber

private const val LOG_TAG = "AnalyticsWorker"
class AnalyticsWorker(
    private val config: AnalyticsConfig,
    private val syncer: Syncer,
    private val dbAdapter: DbAdapter,
) {
    companion object {
        private const val EVENT_CLEANUP_EVENTS: Int = 1
        private const val EVENT_SUBMIT: Int = 2
    }

    private val analyticsHandler: AnalyticsMessageHandler

    init {
        val handlerThread = HandlerThread("Analytics-Worker", HandlerThread.MIN_PRIORITY)
        handlerThread.start()
        analyticsHandler = AnalyticsMessageHandler(handlerThread.looper)
    }


    fun sendEvent(name: String, params: Map<String, String>) {
        val message = analyticsHandler.obtainMessage(EVENT_SUBMIT)
        message.obj = EventMessage(name, params)
        analyticsHandler.sendMessage(message)
    }

    fun cleanup() {
        analyticsHandler.sendMessage(analyticsHandler.obtainMessage(EVENT_CLEANUP_EVENTS))
    }

    private inner class AnalyticsMessageHandler(looper: Looper) : android.os.Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                EVENT_SUBMIT -> {
                    Timber.tag(LOG_TAG).i("Event submit")
                    (msg.obj as? EventMessage)?.let {
                        dbAdapter.addEvent(it.name, it.params)
                    }
                    val dbSize = dbAdapter.getTotalEventCount()
                    if (dbSize > config.foregroundSyncBatchSize && syncer.shouldSyncEventsImmediately()) {
                        sendMessage(obtainMessage(EVENT_CLEANUP_EVENTS))
                    } else if (!hasMessages(EVENT_CLEANUP_EVENTS)) {
                        sendMessageDelayed(
                            obtainMessage(EVENT_CLEANUP_EVENTS),
                            config.foregroundSyncInterval
                        )
                    }
                }

                EVENT_CLEANUP_EVENTS -> {
                    Timber.tag(LOG_TAG).i("Sync events with server")
                    syncer.syncDbEvents()
                }

                else -> {

                }
            }
        }
    }

    data class EventMessage(val name: String, val params: Map<String, String>)
}