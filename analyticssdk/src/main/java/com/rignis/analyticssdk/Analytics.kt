package com.rignis.analyticssdk

interface Analytics {
    fun sendEvent(
        event: String,
        params: Map<String, String>,
    )

    companion object {
        fun getInstance(): Analytics = RignisAnalytics.getInstance()
    }
}
