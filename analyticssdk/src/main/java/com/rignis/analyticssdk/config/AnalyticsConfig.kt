package com.rignis.analyticssdk.config

internal class AnalyticsConfig(
    val clientId: String,
    val batchSize: Int,
    val baseUrl: String,
    val eventDataTTL: Long,
    val flushInterval: Long,
    val flushFallbackInterval: Long,
    val debug: Boolean = true
)
