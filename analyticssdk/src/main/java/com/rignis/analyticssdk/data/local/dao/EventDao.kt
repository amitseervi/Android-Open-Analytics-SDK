package com.rignis.analyticssdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rignis.analyticssdk.data.local.entities.EventEntity
import com.rignis.analyticssdk.data.local.entities.SyncStatus

@Dao
abstract class EventDao {
    @Insert
    abstract fun insertEvent(vararg events: EventEntity)

    @Query("SELECT * FROM rignis_events WHERE sync_status=:syncStatus ORDER BY `client_time_stamp` LIMIT :limit")
    abstract fun readBatch(syncStatus: SyncStatus, limit: Int): List<EventEntity>

    @Query("SELECT COUNT(*) FROM rignis_events WHERE 1")
    abstract fun countTotalEvents(): Int

    @Query("DELETE FROM rignis_events WHERE client_time_stamp<:timeStamp")
    abstract fun deleteEventsBefore(timeStamp: Long)

    @Query("DELETE FROM rignis_events WHERE sync_status=:status")
    abstract fun deleteEventsWithStatus(status: SyncStatus)

    @Query("UPDATE rignis_events SET sync_status=:status WHERE eventId in (:items)")
    abstract fun setSyncStatus(items: List<Int>, status: SyncStatus)

    @Query("UPDATE rignis_events SET sync_status=:status WHERE sync_status=:previous")
    abstract fun changeSyncStatus(previous: SyncStatus,status: SyncStatus)

}
