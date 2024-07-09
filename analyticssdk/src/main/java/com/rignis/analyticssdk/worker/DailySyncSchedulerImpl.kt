/*
 * Copyright (c) [2024] Amitkumar Chaudhary
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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