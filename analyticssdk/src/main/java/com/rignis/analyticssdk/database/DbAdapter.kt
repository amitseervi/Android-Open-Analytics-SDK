package com.rignis.analyticssdk.database

import androidx.annotation.WorkerThread
import com.rignis.analyticssdk.config.AnalyticsConfig

internal class DbAdapter(private val dao: EventDao, private val config: AnalyticsConfig) {
    @WorkerThread
    fun getTotalEventCount(): Int {
        return dao.countTotalEvents()
    }

    @WorkerThread
    fun resetFailedRequest() {
        dao.resetSyncStatusForRequestBefore(System.currentTimeMillis() - config.syncRequestTimeOut)
    }

    @WorkerThread
    fun hasPendingRequest(): Boolean {
        return dao.countInProgressEvents() > 0
    }

    @WorkerThread
    fun readFirstNEvents(n: Int): RequestBatch {
        val time = System.currentTimeMillis()
        return RequestBatch(time, dao.readBatchAndUpdate(n, time))
    }

    @WorkerThread
    fun handleBatchRequestFail(batch: RequestBatch) {
        dao.updateEventOnRequestFail(SyncStatus.PENDING, batch.timeStamp)
    }

    @WorkerThread
    fun handleBatchRequestSuccess(batch: RequestBatch) {
        dao.deleteEventOnRequestSuccess(batch.timeStamp)
    }

    @WorkerThread
    fun addEvent(name: String, params: Map<String, String>) {
        dao.insertEvent(
            EventEntity(
                name,
                params,
                System.currentTimeMillis(),
                SyncStatus.PENDING,
                0L
            )
        )
    }
}