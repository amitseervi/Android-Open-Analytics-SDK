package com.rignis.analyticssdk.client

import com.rignis.analyticssdk.Analytics
import com.rignis.analyticssdk.config.AnalyticsConfig
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
}
