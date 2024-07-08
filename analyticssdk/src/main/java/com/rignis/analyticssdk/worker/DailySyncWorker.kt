package com.rignis.analyticssdk.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rignis.analyticssdk.RignisAnalytics

class DailySyncWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    override fun doWork(): Result {
        if (runAttemptCount > 3) {
            return Result.failure()
        }
        val syncer = RignisAnalytics.syncer
        val result = syncer.syncDbEvents()
        if (result) {
            return Result.success()
        }
        return Result.retry()
    }
}