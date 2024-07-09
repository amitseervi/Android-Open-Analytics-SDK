package com.rignis.analyticssdk.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rignis.analyticssdk.config.AnalyticsConfig
import java.time.Duration

internal class DailySyncSchedulerImpl(private val context: Context) : DailySyncScheduler {
    override fun schedule(config: AnalyticsConfig) {
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

}