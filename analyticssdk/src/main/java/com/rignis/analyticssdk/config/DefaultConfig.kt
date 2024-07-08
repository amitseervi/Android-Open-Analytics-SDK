package com.rignis.analyticssdk.config

object DefaultConfig {
    const val flushFallbackInterval: Long = 60 * 1000
    const val flushInterval: Long = 10 * 1000
    const val batchSize: Int = 10
    const val backgroundSyncEnabled: Boolean = true // sync in background
    const val syncInterval: Long = 5 * 1000 // 5 seconds
    const val baseUrl = "http://192.168.0.194:3000"
    const val eventDataTTL: Long = 30 * 24 * 60 * 60 * 1000L
}
