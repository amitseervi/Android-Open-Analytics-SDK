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
package com.rignis.analyticssdk.config

internal class AnalyticsConfig {
    var eventLifeExpiryTime: Long = DefaultConfig.EVENT_EXPIRY_TIME
    var clientId: String = ""
        private set
    var baseUrl: String = DefaultConfig.SYNC_REQUEST_BASE_URL
    var syncRequestTimeOut: Long= DefaultConfig.SYNC_REQUEST_TIME_OUT
    var backgroundSyncEnabled: Boolean = DefaultConfig.BACKGROUND_SYNC_ENABLED
    var foregroundSyncInterval: Long = DefaultConfig.FOREGROUND_SYNC_INTERVAL
    var foregroundSyncBatchSize: Int = DefaultConfig.FOREGROUND_SYNC_BATCH_SIZE
    var backgroundSyncIntervalInHour: Int = DefaultConfig.BACKGROUND_SYNC_INTERVAL_IN_HOUR
    var maxSyncRequestEventListSize:Int = DefaultConfig.MAX_SYNC_REQ_EVENT_LIST_SIZE
    var optOutAnalytics:Boolean = DefaultConfig.OPT_OUT_ANALYTICS

    val platformName: String
        get() = PlatformVariables.PLATFORM_NAME

    fun setFrom(metaDataReader: MetaDataReader) {
        clientId = metaDataReader.getClientId().orEmpty()
    }
}