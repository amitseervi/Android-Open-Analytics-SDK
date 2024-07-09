package com.rignis.analyticssdk

import android.content.Context
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.MetaDataReaderImpl
import com.rignis.analyticssdk.di.configModule
import com.rignis.analyticssdk.di.dbModule
import com.rignis.analyticssdk.di.networkModule
import com.rignis.analyticssdk.di.syncerModule
import com.rignis.analyticssdk.di.workerModule
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.DailySyncScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent

object RignisAnalytics {
    internal val config: AnalyticsConfig = AnalyticsConfig()
    private val analyticsWorker by KoinJavaComponent.inject<AnalyticsWorker>(AnalyticsWorker::class.java)
    private val dailySyncScheduler by KoinJavaComponent.inject<DailySyncScheduler>(
        DailySyncScheduler::class.java
    )

    fun setBackgroundSyncEnabled(enabled: Boolean) {
        config.backgroundSyncEnabled = enabled
    }

    fun setForegroundSyncInterval(interval: Long) {
        config.foregroundSyncInterval = interval
    }

    fun setForegroundSyncBatchSize(size: Int) {
        config.foregroundSyncBatchSize = size
    }

    fun setBackgroundSyncIntervalInHour(value: Int) {
        config.backgroundSyncIntervalInHour = value
    }

    fun setBackgroundSyncBatchSize(size: Int) {
        config.maxSyncRequestEventListSize = size
    }

    fun optOutAnalytics(optOut: Boolean) {
        config.optOutAnalytics = optOut
    }

    fun setBaseUrl(baseUrl: String) {
        config.baseUrl = baseUrl
    }

    private fun initializeDi(context: Context) {
        startKoin {
            androidContext(context)
            modules(
                networkModule,
                workerModule,
                syncerModule,
                configModule(config),
                dbModule,
            )
        }
    }

    fun initialize(context: Context) {
        config.setFrom(MetaDataReaderImpl(context))
        assert(config.baseUrl.isNotEmpty()) {
            "Base Url can not be empty"
        }
        assert(config.clientId.isNotEmpty()) {
            "Client id should be provided in meta data of application"
        }
        initializeDi(context)
        analyticsWorker.cleanup()
        dailySyncScheduler.schedule(config)
    }

    fun sendEvent(name: String, params: Map<String, String>) {
        if (config.optOutAnalytics) {
            return
        }
        analyticsWorker.sendEvent(name, params)
    }
}