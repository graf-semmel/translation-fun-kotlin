package com.grafsemmel.translationfun.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

/**
 * Single live data resource to track the internet connection state of the device.
 */
class ConnectivityLiveData(private val context: Context) : LiveData<Boolean>() {
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val activeNetwork = intent.extras?.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo
                value = activeNetwork.isConnected
            }
        }
    }

    override fun onActive() {
        super.onActive()
        context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }
}