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

private const val ONE_DAY: Long = 24 * 60 * 60 * 1000
private const val TEN_DAY: Long = 10 * ONE_DAY
object DefaultConfig {
    const val EVENT_EXPIRY_TIME: Long = TEN_DAY
    const val SYNC_REQUEST_BASE_URL: String = ""
    const val SYNC_REQUEST_TIME_OUT: Long = 30 * 1000
    const val OPT_OUT_ANALYTICS: Boolean = false
    const val MAX_SYNC_REQ_EVENT_LIST_SIZE: Int = 30
    const val BACKGROUND_SYNC_ENABLED: Boolean = true
    const val BACKGROUND_SYNC_INTERVAL_IN_HOUR: Int = 4
    const val FOREGROUND_SYNC_BATCH_SIZE: Int = 10
    const val FOREGROUND_SYNC_INTERVAL: Long = 5 * 1000
}