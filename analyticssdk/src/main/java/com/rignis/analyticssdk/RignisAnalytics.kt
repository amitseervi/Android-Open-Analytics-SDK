package com.rignis.analyticssdk

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.MetaDataReader
import com.rignis.analyticssdk.config.MetaDataReaderImpl
import com.rignis.analyticssdk.di.RignisIsolationContext
import com.rignis.analyticssdk.di.RignisKoinComponent
import com.rignis.analyticssdk.di.configModule
import com.rignis.analyticssdk.di.dbModule
import com.rignis.analyticssdk.di.networkModule
import com.rignis.analyticssdk.di.syncerModule
import com.rignis.analyticssdk.di.workerModule
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.DailySyncScheduler
import org.koin.android.ext.koin.androidContext

object RignisAnalytics {
    internal val config: AnalyticsConfig = AnalyticsConfig()

    @VisibleForTesting
    internal var metaDataReaderBuilder: (context: Context) -> MetaDataReader = { context ->
        MetaDataReaderImpl(context)
    }
    private val analyticsWorker by RignisKoinComponent.inject<AnalyticsWorker>(AnalyticsWorker::class.java)
    private val dailySyncScheduler by RignisKoinComponent.inject<DailySyncScheduler>(
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

    fun setNetworkRequestPayloadSize(size: Int) {
        config.maxSyncRequestEventListSize = size
    }

    fun optOutAnalytics(optOut: Boolean) {
        config.optOutAnalytics = optOut
    }

    fun setBaseUrl(baseUrl: String) {
        config.baseUrl = baseUrl
    }

    fun setEventExpiryTime(eventExpiryTime: Long) {
        config.eventLifeExpiryTime = eventExpiryTime
    }

    fun setNetworkRequestTimeout(timeOutInMillis: Long) {
        config.syncRequestTimeOut = timeOutInMillis
    }

    private fun initializeDi(context: Context) {
        RignisIsolationContext.startKoin {
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
        config.setFrom(metaDataReaderBuilder(context))
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