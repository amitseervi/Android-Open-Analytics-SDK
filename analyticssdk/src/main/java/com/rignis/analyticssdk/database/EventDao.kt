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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal abstract class EventDao {
    @Insert
    abstract fun insertEvent(vararg events: EventEntity)

    @Query("SELECT COUNT(*) FROM rignis_events WHERE 1")
    abstract fun countTotalEvents(): Int

    @Query("SELECT COUNT(*) FROM rignis_events WHERE sync_status=\"IN_PROGRESS\"")
    abstract fun countInProgressEvents(): Int

    @Query("SELECT * FROM rignis_events ORDER BY `client_time_stamp` LIMIT :limit")
    abstract fun readBatch(limit: Int): List<EventEntity>

    @Query("UPDATE rignis_events SET sync_status=\"PENDING\" WHERE sync_status=\"IN_PROGRESS\" AND request_start_time<:timeStamp")
    abstract fun resetSyncStatusForRequestBefore(timeStamp: Long)

    @Query("UPDATE rignis_events SET sync_status=:status, request_start_time=:requestTime WHERE eventId in (:items)")
    abstract fun updateEventForSyncRequest(items: List<Long>, status: SyncStatus, requestTime: Long)

    @Query("UPDATE rignis_events SET sync_status=:status,request_start_time=0 WHERE request_start_time=:requestTime")
    abstract fun updateEventOnRequestFail(status: SyncStatus, requestTime: Long)

    @Transaction
    open fun readBatchAndUpdate(limit: Int, timeStamp: Long): List<EventEntity> {
        val result = readBatch(limit)
        updateEventForSyncRequest(
            result.mapNotNull { it.eventId },
            SyncStatus.IN_PROGRESS,
            timeStamp
        )
        return result
    }

    @Query("DELETE FROM rignis_events WHERE request_start_time=:requestTime")
    abstract fun deleteEventOnRequestSuccess(requestTime: Long)

    @Query("DELETE FROM rignis_events WHERE client_time_stamp<:timestamp")
    abstract fun deleteEventBeforeTimeStamp(timestamp: Long)
}