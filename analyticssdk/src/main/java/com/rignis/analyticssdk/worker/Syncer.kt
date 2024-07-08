package com.rignis.analyticssdk.worker

import android.os.Handler
import android.os.Looper
import com.google.gson.JsonObject
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DbAdapter
import com.rignis.analyticssdk.database.EventEntity
import com.rignis.analyticssdk.database.RequestBatch
import com.rignis.analyticssdk.network.ApiService
import com.rignis.analyticssdk.network.EventDto
import com.rignis.analyticssdk.network.SyncRequestPayloadDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Syncer(
    private val dbAdapter: DbAdapter,
    private val apiService: ApiService,
    private val config: AnalyticsConfig
) {
    private var mCallInProgress: Boolean = false
    private var mLastSyncFailed: Boolean = false
    private var mLastSyncFailTimeStamp: Long = 0L
    private var mLastSyncTriggerTimeStamp: Long = 0L


    fun shouldSyncEventsImmediately(): Boolean {
        if (mCallInProgress) {
            return false
        }
        if (System.currentTimeMillis() - mLastSyncTriggerTimeStamp < config.syncRequestDebounceTime) {
            return false
        }
        if (mLastSyncFailed) {
            // TODO : keep increasing fallback time on fail cases
            if (System.currentTimeMillis() - mLastSyncTriggerTimeStamp < config.foregroundSyncInterval) {
                return false
            }
        }
        return true
    }

    fun syncDbEvents(): Boolean {
        if (!shouldSyncEventsImmediately()) {
            return false
        }
        mLastSyncTriggerTimeStamp = System.currentTimeMillis()
        mCallInProgress = true
        return true
    }

    private fun sync() {
        val looper = Looper.myLooper() ?: return
        val batch = dbAdapter.readFirstNEvents(config.maxSyncRequestEventListSize)
        val call = apiService.postEvent(SyncRequestPayloadDto(batch.events.map {
            it.toSyncRequestPayload()
        }))
        val handler = Handler(looper)
        call.enqueue(object : Callback<Response<JsonObject>> {
            override fun onResponse(
                request: Call<Response<JsonObject>>,
                response: Response<Response<JsonObject>>
            ) {
                handler.post {
                    onRequestSuccess(batch)
                }
            }

            override fun onFailure(request: Call<Response<JsonObject>>, error: Throwable) {
                handler.post {
                    onRequestFailed(batch)
                }
            }
        })
    }

    private fun onRequestFailed(batch: RequestBatch) {
        dbAdapter.handleBatchRequestFail(batch)
        mCallInProgress = false
        mLastSyncFailed = true
        mLastSyncFailTimeStamp = System.currentTimeMillis()
    }

    private fun onRequestSuccess(batch: RequestBatch) {
        dbAdapter.handleBatchRequestFail(batch)
        mCallInProgress = false
        mLastSyncFailed = false
        mLastSyncFailTimeStamp = 0L
    }

}

private fun EventEntity.toSyncRequestPayload(): EventDto {
    return EventDto(
        this.eventName,
        this.eventParams,
        this.clientTimeStamp,
    )
}
