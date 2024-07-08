package com.rignis.analyticssdk.network

import com.rignis.analyticssdk.config.AnalyticsConfig
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val config: AnalyticsConfig) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("rignis-client-id", config.clientId)
            .build()
        return chain.proceed(request)
    }
}