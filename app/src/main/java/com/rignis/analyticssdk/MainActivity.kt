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
package com.rignis.analyticssdk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rignis.analyticssdk.ui.page.HomePage
import com.rignis.analyticssdk.ui.theme.AnalyticsSdkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnalyticsSdkTheme {
                HomePage(::testFunction)
            }
        }
    }

    private fun testFunction() {
        Timber.tag("amittest").i("Enqueue work")
        val periodicWorkRequest = PeriodicWorkRequestBuilder<SampleWork>(
            Duration.ofHours(16),
            Duration.ZERO
        ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofMinutes(10))
            .setInitialDelay(Duration.ZERO)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("sample-work", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,periodicWorkRequest)
//        val workRequest = OneTimeWorkRequestBuilder<SampleWork>().build()
//        WorkManager.getInstance(this)
//            .enqueueUniqueWork(
//                "sample-work-one-time",
//                ExistingWorkPolicy.REPLACE,
//                workRequest
//            )
    }
}

class SampleWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Timber.tag("amittest").i("Sample work doing work")
        delay(1000)
        return Result.success()
    }

}