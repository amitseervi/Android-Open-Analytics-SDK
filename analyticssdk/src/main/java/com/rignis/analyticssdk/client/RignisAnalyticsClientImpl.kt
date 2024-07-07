package com.rignis.analyticssdk.client

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.rignis.analyticssdk.Analytics
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.config.DefaultConfig
import com.rignis.analyticssdk.data.local.RignisDb
import com.rignis.analyticssdk.data.local.entities.EventEntity
import com.rignis.analyticssdk.data.local.entities.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class RignisAnalyticsClientImpl(
    private val config: AnalyticsConfig,
    private val db: RignisDb,
) : Analytics {
    private val dao = db.eventDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun sendEvent(event: String, params: Map<String, String>) {
        scope.launch(Dispatchers.IO) {
            dao.insertEvent(
                EventEntity(
                    eventName = event,
                    eventParams = params,
                    syncStatus = SyncStatus.SYNC_PENDING,
                    clientTimeStamp = System.currentTimeMillis()
                )
            )
        }
    }

    companion object {
        internal fun buildFrom(context: Context): RignisAnalyticsClientImpl? {
            val applicationInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val metaDataBundle = applicationInfo.metaData ?: return null
            val clientId = metaDataBundle.getString(CONFIG_ARG_CLIENT_ID)
            if (clientId.isNullOrEmpty()) {
                error("Client id not specified. Add meta data argument in manifest for $CONFIG_ARG_CLIENT_ID")
            }
            val batchSize =
                metaDataBundle.getString(CONFIG_ARG_BATCH_SIZE, "10").toIntOrNull()
                    ?: DefaultConfig.batchSize

            val config = AnalyticsConfig(clientId, batchSize)
            val db =
                Room.databaseBuilder(context.applicationContext, RignisDb::class.java, "rignis-db")
                    .build()
            return RignisAnalyticsClientImpl(config, db)
        }

        private const val CONFIG_ARG_CLIENT_ID = "com.rignis.analyticssdk.config.clientid"
        private const val CONFIG_ARG_BATCH_SIZE = "com.rignis.analyticssdk.config.batchsize"
    }
}
