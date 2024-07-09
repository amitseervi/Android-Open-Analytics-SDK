package com.rignis.analyticssdk.network

interface NetworkConnectivityObserver {
    val isNetworkAvailable: Boolean
    fun addCallback(callback: Callback)

    interface Callback {
        fun onNetworkAvailable()
    }
}