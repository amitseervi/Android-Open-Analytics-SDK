package com.rignis.analyticssdk

import android.content.Context
import androidx.startup.Initializer

class RignisAnalyticsInitializer : Initializer<Analytics> {
    override fun create(context: Context): Analytics {
        RignisAnalytics.setInstance(RignisAnalytics())
        return RignisAnalytics.getInstance()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}