package com.rignis.analyticssdk.config

object DefaultConfig {
    const val maxPostBoxyEventListSize: Int = 50
    const val flushFallbackInterval: Long = 60 * 1000
    const val flushInterval: Long = 5 * 1000
    const val batchSize: Int = 20
    const val baseUrl = "http://192.168.0.194:3000"
    const val eventDataTTL: Long = 30 * 24 * 60 * 60 * 1000L
}
