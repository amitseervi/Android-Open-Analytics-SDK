package com.rignis.analyticssdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rignis_events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val index: Int? = null,
    @ColumnInfo("event_name")
    val eventName: String,
    @ColumnInfo("event_params")
    val eventParams: Map<String, String>,
    @ColumnInfo("sync_status")
    val syncStatus: SyncStatus,
    @ColumnInfo("client_time_stamp")
    val clientTimeStamp: Long,
)
