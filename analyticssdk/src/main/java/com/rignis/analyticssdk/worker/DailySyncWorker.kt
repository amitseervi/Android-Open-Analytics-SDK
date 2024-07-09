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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rignis.analyticssdk.di.RignisKoinComponent
import kotlin.coroutines.suspendCoroutine

internal class DailySyncWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val syncer: Syncer by RignisKoinComponent.inject(Syncer::class.java)
    override suspend fun doWork(): Result {
        if (runAttemptCount > 3) {
            return Result.failure()
        }
        try {
            syncEvents()
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private suspend fun syncEvents(): Boolean {
        return suspendCoroutine { cont ->
            val callback = object : Syncer.OnRequestCompleteCallback {
                override fun onSuccess() {
                    cont.resumeWith(kotlin.Result.success(true))
                }

                override fun onFail(exception: Exception) {
                    cont.resumeWith(kotlin.Result.failure(exception))
                }
            }
            syncer.sync(callback)
        }
    }

}