package com.rignis.analyticssdk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rignis.analyticssdk.RignisAnalytics
import kotlin.coroutines.suspendCoroutine

internal class DailySyncWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
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
            RignisAnalytics.syncer.sync(callback)
        }
    }

}