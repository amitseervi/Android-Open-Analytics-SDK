package com.rignis.analyticssdk.config

class AnalyticsConfig private constructor(
    val backgroundSyncEnabled: Boolean,
    val syncInterval: Long,
) {
    class Builder {
        private var backgroundSyncEnabled = DefaultConfig.backgroundSyncEnabled
        private var syncInterval: Long = DefaultConfig.syncInterval

        fun setBackgroundSyncEnabled(value: Boolean): Builder {
            this.backgroundSyncEnabled = value
            return this
        }

        fun setSyncInterval(value: Long): Builder {
            this.syncInterval = value
            return this
        }

        fun build(): AnalyticsConfig = AnalyticsConfig(backgroundSyncEnabled, syncInterval)
    }
}
