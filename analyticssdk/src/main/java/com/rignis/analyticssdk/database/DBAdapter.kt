package com.rignis.analyticssdk.database

internal interface DBAdapter {
    fun getTotalEventCount(): Int
    fun resetFailedRequest()
    fun hasPendingRequest(): Boolean
    fun readFirstNEvents(n: Int): RequestBatch
    fun handleBatchRequestFail(batch: RequestBatch)
    fun handleBatchRequestSuccess(batch: RequestBatch)
    fun addEvent(name: String, params: Map<String, String>)
}