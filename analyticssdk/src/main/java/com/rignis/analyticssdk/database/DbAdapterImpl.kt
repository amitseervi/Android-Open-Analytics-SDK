package com.rignis.analyticssdk.database

import androidx.annotation.WorkerThread
import com.rignis.analyticssdk.config.AnalyticsConfig

internal class DbAdapterImpl(private val dao: EventDao, private val config: AnalyticsConfig) :
    DBAdapter {
    @WorkerThread
    override fun getTotalEventCount(): Int {
        return dao.countTotalEvents()
    }

    @WorkerThread
    override fun resetFailedRequest() {
        dao.resetSyncStatusForRequestBefore(System.currentTimeMillis() - config.syncRequestTimeOut)
    }

    @WorkerThread
    override fun hasPendingRequest(): Boolean {
        return dao.countInProgressEvents() > 0
    }

    @WorkerThread
    override fun readFirstNEvents(n: Int): RequestBatch {
        val time = System.currentTimeMillis()
        return RequestBatch(time, dao.readBatchAndUpdate(n, time))
    }

    @WorkerThread
    override fun handleBatchRequestFail(batch: RequestBatch) {
        dao.updateEventOnRequestFail(SyncStatus.PENDING, batch.timeStamp)
    }

    @WorkerThread
    override fun handleBatchRequestSuccess(batch: RequestBatch) {
        dao.deleteEventOnRequestSuccess(batch.timeStamp)
    }

    @WorkerThread
    override fun addEvent(name: String, params: Map<String, String>) {
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