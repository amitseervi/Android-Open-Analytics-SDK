package com.rignis.analyticssdk.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class EventDao {
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
    fun readBatchAndUpdate(limit: Int, timeStamp: Long): List<EventEntity> {
        val result = readBatch(limit)
        updateEventForSyncRequest(
            result.map { it.eventId },
            SyncStatus.IN_PROGRESS,
            timeStamp
        )
        return result
    }

    @Query("DELETE FROM rignis_events WHERE request_start_time=:requestTime")
    abstract fun deleteEventOnRequestSuccess(requestTime: Long)
}