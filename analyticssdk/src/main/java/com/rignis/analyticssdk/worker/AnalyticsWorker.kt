package com.rignis.analyticssdk.worker

interface AnalyticsWorker {
    fun cleanup()
    fun sendEvent(name: String, params: Map<String, String>)
}