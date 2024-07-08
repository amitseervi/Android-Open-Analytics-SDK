package com.rignis.analyticssdk

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.DefaultConfig
import com.rignis.analyticssdk.data.local.RignisDb
import com.rignis.analyticssdk.data.remote.service.AnalyticsApiService
import com.rignis.analyticssdk.utils.NetworkConnectivitySubscriber
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.time.Duration

object RignisAnalytics : Analytics {
    private const val CONFIG_ARG_CLIENT_ID = "com.rignis.analyticssdk.config.clientid"
    private const val CONFIG_ARG_BATCH_SIZE = "com.rignis.analyticssdk.config.batchsize"

    internal lateinit var db: RignisDb
        private set
    internal lateinit var config: AnalyticsConfig
        private set
    private lateinit var _service: AnalyticsApiService
    internal lateinit var worker: AnalyticsWorker
    internal val networkConnectivitySubscriber: NetworkConnectivitySubscriber =
        NetworkConnectivitySubscriber()

    internal fun init(context: Context) {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val metaDataBundle = applicationInfo.metaData ?: return
        val clientId = metaDataBundle.getString(CONFIG_ARG_CLIENT_ID)
        if (clientId.isNullOrEmpty()) {
            error("Client id not specified. Add meta data argument in manifest for $CONFIG_ARG_CLIENT_ID")
        }
        val batchSize = metaDataBundle.getInt(CONFIG_ARG_BATCH_SIZE, DefaultConfig.BATCH_SIZE)
        config = AnalyticsConfig(
            clientId = clientId,
            batchSize = batchSize,
            baseUrl = DefaultConfig.BASE_URL,
            eventDataTTL = DefaultConfig.EVENT_DATA_TTL,
            flushInterval = DefaultConfig.FLUSH_INTERVAL,
            flushFallbackInterval = DefaultConfig.FLUSH_FALLBACK_INTERVAL,
            maxPostBoxyEventListSize = DefaultConfig.MAX_POST_PAYLOAD_LIST_SIZE
        )
        db = Room.databaseBuilder(context, RignisDb::class.java, "rignis").build()
        networkConnectivitySubscriber.subscribe(context)
        _service = service()
        worker = AnalyticsWorker(db.eventDao(), config, _service, networkConnectivitySubscriber)
        worker.cleanupEvents()
    }

    internal fun service(): AnalyticsApiService {
        if (this::_service.isInitialized) {
            return this._service
        }
        synchronized(this) {
            if (!this::_service.isInitialized) {
                val retrofit = Retrofit.Builder().baseUrl(config.baseUrl).client(
                    OkHttpClient.Builder()
                        .let {
                            if (config.debug) {
                                it.addInterceptor(HttpLoggingInterceptor(logger = HttpLoggingInterceptor.Logger { message ->
                                    Timber.tag("Rignis-network").i(message)
                                }))
                            } else {
                                it
                            }
                        }
                        .callTimeout(Duration.ofSeconds(10))
                        .build()
                ).addConverterFactory(GsonConverterFactory.create())
                    .build()
                _service = retrofit.create<AnalyticsApiService>(AnalyticsApiService::class.java)
            }
            return _service
        }
    }

    override fun sendEvent(
        event: String,
        params: Map<String, String>,
    ) {
        worker.sendEvent(event, params)
    }
}
