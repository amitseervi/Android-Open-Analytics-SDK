package com.rignis.analyticssdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rignis_events")
data class EventEntity(
    @ColumnInfo("event_name")
    val eventName: String,
    @ColumnInfo("event_params")
    val eventParams: Map<String, String>,
    @ColumnInfo("client_time_stamp")
    val clientTimeStamp: Long,
    @ColumnInfo("sync_status")
    val status: SyncStatus,
    @PrimaryKey(autoGenerate = true)
    val eventId: Int? = null,
)
