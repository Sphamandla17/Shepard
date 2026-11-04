package com.example.shepherd

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object NetworkUtils {

    /**
     * Checks whether an active network has NET_CAPABILITY_INTERNET and NET_CAPABILITY_VALIDATED.
     * Distinguishes physical link connectivity from genuine validated internet access.
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Registers a ConnectivityManager.NetworkCallback with precise state tracking:
     * - onAvailable: Physical link available (does not assume internet).
     * - onValidated: Genuine validated internet access achieved (transition from unvalidated to validated).
     * - onInternetLost: A previously validated connection lost its validation.
     * - onLost: Network connection completely lost/unavailable.
     */
    fun registerNetworkCallback(
        context: Context,
        onAvailable: () -> Unit = {},
        onValidated: () -> Unit = {},
        onInternetLost: () -> Unit = {},
        onLost: () -> Unit = {}
    ): ConnectivityManager.NetworkCallback? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return null

        var wasValidated = false

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onAvailable()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                if (isValidated) {
                    if (!wasValidated) {
                        wasValidated = true
                        onValidated()
                    }
                } else {
                    if (wasValidated) {
                        wasValidated = false
                        onInternetLost()
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                wasValidated = false
                onLost()
            }
        }

        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            return networkCallback
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Safely unregisters the NetworkCallback to avoid memory leaks.
     */
    fun unregisterNetworkCallback(
        context: Context,
        networkCallback: ConnectivityManager.NetworkCallback?
    ) {
        if (networkCallback == null) return
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return

        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Callback was already unregistered or manager invalid
        }
    }
}
