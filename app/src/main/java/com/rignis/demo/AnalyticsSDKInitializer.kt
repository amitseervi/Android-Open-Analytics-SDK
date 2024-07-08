package com.rignis.demo

import android.content.Context
import androidx.startup.Initializer
import com.rignis.analyticssdk.RignisAnalytics

class AnalyticsSDKInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        RignisAnalytics.setBaseUrl("http://192.168.0.194:3000")
        RignisAnalytics.initialize(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}