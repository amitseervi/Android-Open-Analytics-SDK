package com.rignis.analyticssdk.config

internal class AnalyticsConfig(
    val clientId: String,
    batchSize: Int,
    val baseUrl: String,
    eventDataTTL: Long,
    flushInterval: Long,
    flushFallbackInterval: Long,
    val debug: Boolean = false,
    maxPostBoxyEventListSize: Int
) {
    private var _batchSize: Int = batchSize
    val batchSize: Int
        get() = _batchSize

    private var _maxPostBoxyEventListSize: Int = maxPostBoxyEventListSize
    val maxPostBoxyEventListSize: Int
        get() = _maxPostBoxyEventListSize

    private var _eventDataTTL: Long = eventDataTTL
    val eventDataTTL: Long
        get() = _eventDataTTL

    private var _flushInterval: Long = flushInterval
    val flushInterval: Long
        get() = _flushInterval

    private var _flushFallbackInterval: Long = flushFallbackInterval
    val flushFallbackInterval: Long
        get() = _flushFallbackInterval
}
