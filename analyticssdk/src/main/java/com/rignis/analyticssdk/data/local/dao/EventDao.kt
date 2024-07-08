package com.rignis.analyticssdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rignis.analyticssdk.data.local.entities.EventEntity

@Dao
abstract class EventDao {
    @Insert
    abstract fun insertEvent(vararg events: EventEntity)

    @Query("SELECT * FROM rignis_events ORDER BY `index` LIMIT :batchSize")
    abstract fun readBatch(batchSize: Int): List<EventEntity>

    @Query("SELECT COUNT(*) FROM rignis_events WHERE 1")
    abstract fun countTotalEvents(): Int

    @Query("DELETE FROM rignis_events WHERE client_time_stamp<:timeStamp")
    abstract fun deleteEventsBefore(timeStamp: Long)

    @Query("DELETE FROM rignis_events WHERE `index`<=:index")
    abstract fun deleteEventBeforeId(index: Int)
}
