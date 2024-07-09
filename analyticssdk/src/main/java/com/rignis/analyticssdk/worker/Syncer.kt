package com.rignis.analyticssdk.worker

internal interface Syncer {
    fun sync(callback: OnRequestCompleteCallback)

    interface OnRequestCompleteCallback {
        fun onSuccess()
        fun onFail(exception: Exception)
    }
}