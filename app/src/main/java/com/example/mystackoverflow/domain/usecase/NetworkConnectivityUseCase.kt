package com.example.mystackoverflow.domain.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityUseCase @Inject constructor(
    private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val connectionStatus = PublishSubject.create<Boolean>()
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        observeNetworkConnectivity()
    }

    fun observe(): Observable<Boolean> = connectionStatus

    private fun observeNetworkConnectivity() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectionStatus.onNext(true)
            }

            override fun onLost(network: Network) {
                connectionStatus.onNext(false)
            }

            override fun onUnavailable() {
                connectionStatus.onNext(false)
            }
        }.also { callback ->
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, callback)
        }
    }

    fun cleanup() {
        networkCallback?.let { callback ->
            connectivityManager.unregisterNetworkCallback(callback)
        }
        networkCallback = null
    }
} 