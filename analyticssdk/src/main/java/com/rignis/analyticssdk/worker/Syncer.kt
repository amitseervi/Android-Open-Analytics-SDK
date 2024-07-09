package com.rignis.analyticssdk.worker

import android.os.Handler
import android.os.Looper
import com.google.gson.JsonObject
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DbAdapter
import com.rignis.analyticssdk.database.EventEntity
import com.rignis.analyticssdk.database.RequestBatch
import com.rignis.analyticssdk.error.BadRequestException
import com.rignis.analyticssdk.error.ServerErrorException
import com.rignis.analyticssdk.network.ApiService
import com.rignis.analyticssdk.network.EventDto
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import com.rignis.analyticssdk.network.SyncRequestPayloadDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

private const val LOG_TAG = "Syncer"
internal class Syncer(
    private val dbAdapter: DbAdapter,
    private val apiService: ApiService,
    private val config: AnalyticsConfig,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) {
    fun sync(callback: OnRequestCompleteCallback) {
        if (!networkConnectivityObserver.isNetworkAvailable()) {
            callback.onFail(RuntimeException("Network not available"))
        }
        val looper = Looper.myLooper() ?: return
        dbAdapter.resetFailedRequest()
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
                    if (response.isSuccessful) {
                        onRequestSuccess(batch, callback)
                    } else {
                        val code = response.code()
                        if (code.isServerError()) {
                            onRequestFailed(batch, callback, BadRequestException())
                        } else if (code.isBadStatusCode()) {
                            onRequestFailed(batch, callback, ServerErrorException())
                        } else {
                            onRequestFailed(batch, callback, Exception("Unknown error occured"))
                        }
                    }
                }
            }

            override fun onFailure(request: Call<Response<JsonObject>>, error: Throwable) {
                handler.post {
                    onRequestFailed(batch, callback, Exception(error))
                }
            }
        })
    }

    private fun onRequestFailed(
        batch: RequestBatch,
        callback: OnRequestCompleteCallback,
        exception: Exception
    ) {
        Timber.tag(LOG_TAG).i("Sync db failed")
        dbAdapter.handleBatchRequestFail(batch)
        callback.onFail(exception)
    }

    private fun onRequestSuccess(batch: RequestBatch, callback: OnRequestCompleteCallback) {
        Timber.tag(LOG_TAG).i("Sync db success")
        dbAdapter.handleBatchRequestSuccess(batch)
        callback.onSuccess()
    }

    interface OnRequestCompleteCallback {
        fun onSuccess()
        fun onFail(exception: Exception)
    }
}

private fun Int.isBadStatusCode(): Boolean {
    return this in 400..499
}

private fun Int.isServerError(): Boolean {
    return this in 500..599
}

private fun EventEntity.toSyncRequestPayload(): EventDto {
    return EventDto(
        this.eventName,
        this.eventParams,
        this.clientTimeStamp,
    )
}

