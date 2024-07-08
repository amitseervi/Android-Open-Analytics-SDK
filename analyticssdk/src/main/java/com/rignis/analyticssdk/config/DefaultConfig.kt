package com.rignis.analyticssdk.config

object DefaultConfig {
    const val SYNC_REQUEST_BASE_URL: String = ""
    const val SYNC_REQUEST_DEBOUNCE_TIME: Long = 2000
    const val SYNC_REQUEST_TIME_OUT: Long = 30 * 1000
    const val OPT_OUT_ANALYTICS: Boolean = false
    const val MAX_SYNC_REQ_EVENT_LIST_SIZE: Int = 20
    const val BACKGROUND_SYNC_ENABLED: Boolean = true
    const val BACKGROUND_SYNC_INTERVAL_IN_HOUR: Int = 4
    const val FOREGROUND_SYNC_BATCH_SIZE: Int = 20
    const val FOREGROUND_SYNC_INTERVAL: Long = 5 * 1000
}