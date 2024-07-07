package com.rignis.analyticssdk

import android.content.Context
import com.rignis.analyticssdk.client.RignisAnalyticsClientImpl

object RignisAnalytics : Analytics {
    @Volatile
    private var rignisAnalyticsClientImpl: RignisAnalyticsClientImpl? = null

    @Synchronized
    internal fun init(context: Context) {
        if (rignisAnalyticsClientImpl != null) {
            return
        }
        rignisAnalyticsClientImpl = RignisAnalyticsClientImpl.buildFrom(context)
    }
    override fun sendEvent(
        event: String,
        params: Map<String, String>,
    ) {
        rignisAnalyticsClientImpl?.sendEvent(event, params)
    }
}
