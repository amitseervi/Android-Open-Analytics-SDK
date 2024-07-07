package com.rignis.analyticssdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rignis.analyticssdk.data.local.entities.EventEntity

@Dao
abstract class EventDao {
    @Insert
    abstract suspend fun insertEvent(vararg events: EventEntity)

    @Query("SELECT * FROM rignis_events WHERE sync_status=\"SYNC_PENDING\" ORDER BY `index` LIMIT :batchSize")
    abstract suspend fun readBatch(batchSize: Int): List<EventEntity>

    @Query("SELECT EXISTS(SELECT * FROM RIGNIS_EVENTS where sync_status=\"SYNC_PENDING\" LIMIT 1)")
    abstract suspend fun hasPendingEvents(): Boolean
}
