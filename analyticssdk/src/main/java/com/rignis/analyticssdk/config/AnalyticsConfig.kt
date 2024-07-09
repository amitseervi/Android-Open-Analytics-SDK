package com.rignis.analyticssdk.config

internal class AnalyticsConfig {
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

    fun setFrom(metaDataReader: MetaDataReader) {
        clientId = metaDataReader.getClientId().orEmpty()
    }
}