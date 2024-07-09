package com.rignis.analyticssdk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.java.KoinJavaComponent
import kotlin.coroutines.suspendCoroutine

internal class DailySyncWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val syncer by KoinJavaComponent.inject<Syncer>(Syncer::class.java)
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