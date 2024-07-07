package com.rignis.analyticssdk.config

object DefaultConfig {
    const val batchSize: Int = 10
    const val backgroundSyncEnabled: Boolean = true // sync in background
    const val syncInterval: Long = 5 * 1000 // 5 seconds
}
