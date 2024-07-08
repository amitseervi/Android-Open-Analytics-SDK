package com.rignis.analyticssdk

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.data.local.dao.EventDao
import com.rignis.analyticssdk.data.local.entities.EventEntity
import com.rignis.analyticssdk.data.local.entities.SyncStatus
import com.rignis.analyticssdk.data.remote.dto.SyncRequestDto
import com.rignis.analyticssdk.data.remote.dto.SyncRequestPayloadDto
import com.rignis.analyticssdk.data.remote.service.AnalyticsApiService
import com.rignis.analyticssdk.utils.NetworkConnectivitySubscriber
import retrofit2.Call
import retrofit2.Response
import kotlin.math.min


internal class AnalyticsWorker(
    private val dao: EventDao,
    private val config: AnalyticsConfig,
    private val service: AnalyticsApiService,
    private val networkConnectivitySubscriber: NetworkConnectivitySubscriber
) {
    private val mHandler: Handler
    private var lastFlushFailTimeStamp: Long = 0L
    private var lastFlushPushEventTimeStamp: Long = 0L

    init {
        val handlerThread =
            HandlerThread("com.rignis.analyticssdk.analyticsworker", HandlerThread.MIN_PRIORITY)
        handlerThread.start()
        mHandler = AnalyticsMessageHandler(handlerThread.looper)
    }

    fun sendEvent(name: String, params: Map<String, String>) {
        val message = mHandler.obtainMessage(ENQUEUE_EVENTS)
        message.obj = EventMessage(name, params)
        mHandler.sendMessage(message)
    }

    fun flushEvents() {
        val message = mHandler.obtainMessage(FLUSH_EVENTS)
        mHandler.sendMessage(message)
    }

    fun cleanupEvents() {
        val message = mHandler.obtainMessage(CLEANUP_EVENTS)
        mHandler.sendMessage(message)
    }

    companion object {
        private const val FLUSH_EVENTS = 1
        private const val ENQUEUE_EVENTS = 2
        private const val CLEANUP_EVENTS = 3
        private const val FLUSH_EVENT_SUCCESS = 4
        private const val FLUSH_EVENT_FAILED = 5
    }


    private data class EventMessage(val name: String, val params: Map<String, String>)


    inner class AnalyticsMessageHandler(looper: Looper) : Handler(looper),
        NetworkConnectivitySubscriber.Callback {
        private var flushRetry: Int = 0
        private var flushInProgress: Boolean = false

        init {
            networkConnectivitySubscriber.addCallback(this)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ENQUEUE_EVENTS -> {
                    val eventMessage = msg.obj as EventMessage
                    dao.insertEvent(
                        EventEntity(
                            eventMessage.name,
                            eventMessage.params,
                            System.currentTimeMillis(),
                            SyncStatus.SYNC_PENDING
                        )
                    )
                    val totalEvents = dao.countTotalEvents()
                    if (totalEvents >= config.batchSize && System.currentTimeMillis() - lastFlushFailTimeStamp >= config.flushInterval && System.currentTimeMillis() - lastFlushPushEventTimeStamp > 2000) {
                        lastFlushPushEventTimeStamp = System.currentTimeMillis()
                        val flushMessage = obtainMessage(FLUSH_EVENTS)
                        sendMessage(flushMessage)
                    } else if (totalEvents > 0 && !hasMessages(FLUSH_EVENTS)) {
                        val autoFlushMessage = obtainMessage(FLUSH_EVENTS)
                        sendMessageDelayed(autoFlushMessage, config.flushInterval)
                    }
                }

                FLUSH_EVENTS -> {
                    if (flushInProgress) {
                        return
                    }
                    val eventBatch =
                        dao.readBatch(SyncStatus.SYNC_PENDING, config.maxPostBoxyEventListSize)
                    if (eventBatch.isNotEmpty()) {
                        if (networkConnectivitySubscriber.isNetworkAvailable() && !flushInProgress) {
                            flushInProgress = true
                            dao.setSyncStatus(
                                eventBatch.mapNotNull { it.eventId },
                                SyncStatus.SYNC_IN_PROGRESS
                            )
                            val call =
                                service.postEvent(SyncRequestPayloadDto(eventBatch.map { it.toDto() }))
                            call.enqueue(object : retrofit2.Callback<Response<String>> {
                                override fun onResponse(
                                    p0: Call<Response<String>>,
                                    p1: Response<Response<String>>
                                ) {
                                    mHandler.sendMessageAtFrontOfQueue(
                                        obtainMessage(
                                            FLUSH_EVENT_SUCCESS
                                        )
                                    )
                                }

                                override fun onFailure(p0: Call<Response<String>>, p1: Throwable) {
                                    mHandler.sendMessageAtFrontOfQueue(
                                        obtainMessage(
                                            FLUSH_EVENT_FAILED
                                        )
                                    )
                                }
                            })
                        }
                    }
                }

                FLUSH_EVENT_SUCCESS -> {
                    lastFlushFailTimeStamp = 0
                    dao.deleteEventsWithStatus(SyncStatus.SYNC_IN_PROGRESS)
                    flushInProgress = false
                    flushRetry = 0
                    val totalEvents = dao.countTotalEvents()
                    if (totalEvents > config.batchSize) {
                        val flushMessage = obtainMessage(FLUSH_EVENTS)
                        sendMessage(flushMessage)
                    } else if (totalEvents > 0) {
                        val flushMessage = obtainMessage(FLUSH_EVENTS)
                        sendMessageDelayed(flushMessage, config.flushInterval)
                    }
                }

                FLUSH_EVENT_FAILED -> {
                    removeMessages(FLUSH_EVENTS)
                    lastFlushFailTimeStamp = System.currentTimeMillis()
                    dao.changeSyncStatus(SyncStatus.SYNC_IN_PROGRESS, SyncStatus.SYNC_PENDING)
                    if (flushRetry < 3) {
                        var flushRetryFallbackDelay = config.flushFallbackInterval
                        repeat(flushRetry + 1) {
                            flushRetryFallbackDelay += flushRetryFallbackDelay
                        }
                        flushRetryFallbackDelay =
                            min(flushRetryFallbackDelay, 10 * 60 * 1000L)
                        val flushMessage = obtainMessage(FLUSH_EVENTS)
                        sendMessageDelayed(flushMessage, flushRetryFallbackDelay)
                        flushRetry++
                    } else {
                        val flushMessage = obtainMessage(FLUSH_EVENTS)
                        sendMessageDelayed(flushMessage, 10 * 60 * 1000L)
                    }
                    flushInProgress = false
                }

                CLEANUP_EVENTS -> {
                    dao.deleteEventsBefore(System.currentTimeMillis() - config.eventDataTTL)
                    val flushMessage = obtainMessage(FLUSH_EVENTS)
                    sendMessage(flushMessage)
                }

                else -> {

                }
            }
        }

        override fun onNetworkAvailable() {
            val flushMessage = obtainMessage(FLUSH_EVENTS)
            sendMessage(flushMessage)
        }
    }
}


private fun EventEntity.toDto(): SyncRequestDto {
    return SyncRequestDto(eventName, eventParams, clientTimeStamp)
}
