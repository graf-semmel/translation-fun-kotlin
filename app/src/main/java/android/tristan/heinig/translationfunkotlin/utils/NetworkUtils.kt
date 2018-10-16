package android.tristan.heinig.translationfunkotlin.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {

  fun isConnected(pContext: Context): Boolean {
    val connectivityManager = pContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = connectivityManager?.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
  }
}
