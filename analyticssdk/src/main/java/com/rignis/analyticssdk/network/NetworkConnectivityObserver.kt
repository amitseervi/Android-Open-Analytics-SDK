package com.rignis.analyticssdk.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.core.content.ContextCompat

class NetworkConnectivityObserver : ConnectivityManager.NetworkCallback() {
    private var mIsNetworkAvailable: Boolean = false
    private val subscriber: MutableSet<Callback> = mutableSetOf()

    fun subscribe(context: Context) {
        val connectivityManager =
            ContextCompat.getSystemService(context, ConnectivityManager::class.java) ?: return
        connectivityManager.registerDefaultNetworkCallback(this)
    }

    fun addCallback(callback: Callback) {
        subscriber.add(callback)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        mIsNetworkAvailable = true
        subscriber.forEach {
            it.onNetworkAvailable()
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        mIsNetworkAvailable = false
    }

    override fun onUnavailable() {
        super.onUnavailable()
        mIsNetworkAvailable = false
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        super.onBlockedStatusChanged(network, blocked)
        mIsNetworkAvailable = !blocked
    }

    fun isNetworkAvailable(): Boolean {
        return mIsNetworkAvailable
    }

    interface Callback {
        fun onNetworkAvailable()
    }
}