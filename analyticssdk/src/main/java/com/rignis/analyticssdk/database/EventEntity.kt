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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rignis_events")
internal class EventEntity(
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