package com.rignis.analyticssdk

import android.content.Context
import android.content.pm.PackageManager
import com.rignis.analyticssdk.client.RignisAnalyticsClientImpl
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.DefaultConfig
import com.rignis.analyticssdk.data.local.RignisDb
import com.rignis.analyticssdk.data.remote.service.AnalyticsApiService
import retrofit2.Retrofit

object RignisAnalytics : Analytics {
    private const val CONFIG_ARG_CLIENT_ID = "com.rignis.analyticssdk.config.clientid"
    private const val CONFIG_ARG_BATCH_SIZE = "com.rignis.analyticssdk.config.batchsize"

    @Volatile
    private var rignisAnalyticsClientImpl: RignisAnalyticsClientImpl? = null

    internal lateinit var db: RignisDb
        private set
    internal lateinit var config: AnalyticsConfig
        private set
    internal lateinit var _service: AnalyticsApiService
        private set

    @Synchronized
    internal fun init(context: Context) {
        if (rignisAnalyticsClientImpl != null) {
            return
        }
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val metaDataBundle = applicationInfo.metaData ?: return
        val clientId = metaDataBundle.getString(CONFIG_ARG_CLIENT_ID)
        if (clientId.isNullOrEmpty()) {
            error("Client id not specified. Add meta data argument in manifest for $CONFIG_ARG_CLIENT_ID")
        }
        val batchSize =
            metaDataBundle.getString(CONFIG_ARG_BATCH_SIZE, "10").toIntOrNull()
                ?: DefaultConfig.batchSize

        config = AnalyticsConfig(clientId, batchSize, DefaultConfig.baseUrl)
        rignisAnalyticsClientImpl = RignisAnalyticsClientImpl(config, db)
    }

    internal fun service(): AnalyticsApiService {
        if (this::_service.isInitialized) {
            return this._service
        }
        synchronized(this) {
            if (!this::_service.isInitialized) {
                val retrofit = Retrofit.Builder().baseUrl(config.baseUrl).build()
                _service = retrofit.create<AnalyticsApiService>(AnalyticsApiService::class.java)
            }
            return _service
        }
    }

    override fun sendEvent(
        event: String,
        params: Map<String, String>,
    ) {
        rignisAnalyticsClientImpl?.sendEvent(event, params)
    }
}
