package com.rignis.analyticssdk

import android.content.Context
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.MetaDataReader
import com.rignis.analyticssdk.database.DbAdapter
import com.rignis.analyticssdk.database.RignisEventDB
import com.rignis.analyticssdk.network.ApiService
import com.rignis.analyticssdk.network.HeaderInterceptor
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.DailySyncWorker
import com.rignis.analyticssdk.worker.Syncer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

object RignisAnalytics {
    private lateinit var dbAdapter: DbAdapter
    internal val config: AnalyticsConfig = AnalyticsConfig()
    internal lateinit var analyticsWorker: AnalyticsWorker
    internal val networkConnectivityObserver = NetworkConnectivityObserver()
    internal lateinit var syncer: Syncer
    internal lateinit var apiService: ApiService
    internal lateinit var db: RignisEventDB

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

    fun initialize(context: Context) {
        config.setFrom(MetaDataReader(context))
        assert(config.baseUrl.isNotEmpty()) {
            "Base Url can not be empty"
        }
        assert(config.clientId.isNotEmpty()) {
            "Client id should be provided in meta data of application"
        }
        networkConnectivityObserver.subscribe(context)
        db = Room.databaseBuilder(context, RignisEventDB::class.java, "rignis_event_db").build()
        val retrofit = Retrofit.Builder().baseUrl(config.baseUrl).client(
            OkHttpClient.Builder()
                .callTimeout(Duration.ofMillis(config.syncRequestTimeOut))
                .addInterceptor(HeaderInterceptor(config))
                .build()
        ).addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
        dbAdapter = DbAdapter(db.eventDao(), config)
        syncer = Syncer(dbAdapter, apiService, config)
        analyticsWorker = restartAnalyticsWorker(config, syncer, dbAdapter)
        analyticsWorker.cleanup()
        enqueBackgroundWorker(context, config)
    }

    private fun enqueBackgroundWorker(context: Context, config: AnalyticsConfig) {
        val workManager = WorkManager.getInstance(context)
        if (!config.backgroundSyncEnabled) {
            workManager.cancelUniqueWork("rignis-data-sync")
            return
        }
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
            Duration.ofHours(config.backgroundSyncIntervalInHour.toLong())
        ).setConstraints(
            Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()
        workManager.enqueueUniquePeriodicWork(
            "rignis-data-sync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun restartAnalyticsWorker(
        config: AnalyticsConfig,
        syncer: Syncer,
        dbAdapter: DbAdapter
    ): AnalyticsWorker {
        if (::analyticsWorker.isInitialized) {
            analyticsWorker.cleanup()
        }
        analyticsWorker = AnalyticsWorker(config, syncer, dbAdapter)
        return analyticsWorker
    }

    fun sendEvent(name: String, params: Map<String, String>) {
        if (config.optOutAnalytics) {
            return
        }
        analyticsWorker.sendEvent(name, params)
    }
}