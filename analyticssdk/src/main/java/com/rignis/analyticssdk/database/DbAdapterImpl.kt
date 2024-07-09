/*
 * Copyright (c) [2024] Amitkumar Chaudhary
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    override fun deleteLongPendingRequests(timestamp: Long) {
        return dao.deleteEventBeforeTimeStamp(timestamp)
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