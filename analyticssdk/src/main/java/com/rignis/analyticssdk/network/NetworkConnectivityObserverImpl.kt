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
package com.rignis.analyticssdk.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.core.content.ContextCompat

internal class NetworkConnectivityObserverImpl(context: Context) :
    ConnectivityManager.NetworkCallback(),
    NetworkConnectivityObserver {
    private var mIsNetworkAvailable: Boolean = true

    override val isNetworkAvailable: Boolean
        get() = mIsNetworkAvailable

    private val subscriber: MutableSet<NetworkConnectivityObserver.Callback> = mutableSetOf()

    init {
        val connectivityManager =
            ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        connectivityManager?.registerDefaultNetworkCallback(this)
    }

    override fun addCallback(callback: NetworkConnectivityObserver.Callback) {
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
}