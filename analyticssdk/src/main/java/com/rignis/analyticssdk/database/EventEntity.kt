package com.rignis.analyticssdk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rignis_events")
class EventEntity(
    @ColumnInfo("event_name")
    val eventName: String,
    @ColumnInfo("event_params")
    val eventParams: Map<String, String>,
    @ColumnInfo("client_time_stamp")
    val clientTimeStamp: Long,
    @ColumnInfo("sync_status")
    val status: SyncStatus,
    @ColumnInfo("request_start_time")
    val requestStartTime: Long,
    @PrimaryKey(autoGenerate = true)
    val eventId: Long? = null,
)