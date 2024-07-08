package com.rignis.analyticssdk.config

object DefaultConfig {
    const val MAX_POST_PAYLOAD_LIST_SIZE: Int = 50
    const val FLUSH_FALLBACK_INTERVAL: Long = 60 * 1000
    const val FLUSH_INTERVAL: Long = 5 * 1000
    const val BATCH_SIZE: Int = 20
    const val BASE_URL = "http://192.168.0.194:3000"
    const val EVENT_DATA_TTL: Long = 30 * 24 * 60 * 60 * 1000L
}
