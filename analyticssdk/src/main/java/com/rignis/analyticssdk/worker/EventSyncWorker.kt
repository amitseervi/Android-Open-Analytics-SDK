package com.rignis.analyticssdk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rignis.analyticssdk.RignisAnalytics

class EventSyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val db = RignisAnalytics.db
    private val config = RignisAnalytics.config
    private val dao = db.eventDao()

    override suspend fun doWork(): Result {
        if (!dao.hasPendingEvents()) {
            return Result.success()
        }
        val events = dao.readBatch(config.batchSize)
        return Result.success()
    }
}