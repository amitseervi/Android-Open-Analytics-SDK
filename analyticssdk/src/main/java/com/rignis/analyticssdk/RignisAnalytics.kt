package com.rignis.analyticssdk

import kotlin.properties.Delegates

class RignisAnalytics : Analytics {
    override fun sendEvent(
        event: String,
        params: Map<String, String>,
    ) {
    }

    companion object {
        private var instance: RignisAnalytics by Delegates.notNull<RignisAnalytics>()

        fun getInstance(): Analytics = instance

        internal fun setInstance(instance: RignisAnalytics) {
            this.instance = instance
        }
    }
}
