package com.rignis.analyticssdk

import android.content.Context
import androidx.room.Room
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.MetaDataReader
import com.rignis.analyticssdk.database.DbAdapter
import com.rignis.analyticssdk.database.RignisEventDB
import com.rignis.analyticssdk.network.ApiService
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.Syncer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
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
        assert(config.baseUrl.isNotEmpty()) {
            "Base Url can not be empty"
        }
        config.setFrom(MetaDataReader(context))
        networkConnectivityObserver.subscribe(context)
        db = Room.databaseBuilder(context, RignisEventDB::class.java, "rignis_event_db").build()
        val retrofit = Retrofit.Builder().baseUrl(config.baseUrl).client(
            OkHttpClient.Builder()
                .callTimeout(Duration.ofMillis(config.syncRequestTimeOut))
                .build()
        ).build()
        apiService = retrofit.create(ApiService::class.java)
        dbAdapter = DbAdapter(db.eventDao(), config)
        syncer = Syncer(dbAdapter, apiService, config)
        analyticsWorker = restartAnalyticsWorker(config, syncer, dbAdapter)
        analyticsWorker.cleanup()
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
    }
}