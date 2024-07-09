package com.rignis.analyticssdk.worker

import com.rignis.analyticssdk.config.AnalyticsConfig

internal interface DailySyncScheduler {
    fun schedule(config: AnalyticsConfig)
}