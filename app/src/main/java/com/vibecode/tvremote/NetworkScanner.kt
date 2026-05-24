package com.vibecode.tvremote

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.TimeUnit

data class DiscoveredTv(
    val ip: String,
    val name: String,
    val model: String? = null,
    val uuid: String? = null
)

class NetworkScanner(private val context: Context) {
    companion object {
        private const val TAG = "NetworkScanner"
    }

    private val gson = Gson()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(600, TimeUnit.MILLISECONDS)
        .readTimeout(600, TimeUnit.MILLISECONDS)
        .build()

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (isIPv4) return sAddr
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error getting IP address", ex)
        }
        return null
    }

    suspend fun scanSubnet(onDeviceFound: (DiscoveredTv) -> Unit, onScanComplete: () -> Unit) {
        val localIp = getLocalIpAddress()
        if (localIp == null) {
            Log.w(TAG, "No local IP address found. Scanner aborted.")
            onScanComplete()
            return
        }

        val lastDotIndex = localIp.lastIndexOf('.')
        if (lastDotIndex <= 0) {
            Log.w(TAG, "Invalid local IP format. Scanner aborted.")
            onScanComplete()
            return
        }

        val subnetPrefix = localIp.substring(0, lastDotIndex + 1)
        
        withContext(Dispatchers.IO) {
            val jobs = mutableListOf<Job>()
            for (i in 1..254) {
                val testIp = "$subnetPrefix$i"
                if (testIp == localIp) continue

                val job = launch {
                    val tv = checkIpForSamsungTv(testIp)
                    if (tv != null) {
                        withContext(Dispatchers.Main) {
                            onDeviceFound(tv)
                        }
                    }
                }
                jobs.add(job)
            }
            jobs.joinAll()
            withContext(Dispatchers.Main) {
                onScanComplete()
            }
        }
    }

    private fun checkIpForSamsungTv(ip: String): DiscoveredTv? {
        val url = "http://$ip:8001/api/v2/"
        val request = Request.Builder().url(url).build()
        try {
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return null
                    val jsonMap = gson.fromJson(body, Map::class.java)
                    val device = jsonMap["device"] as? Map<*, *> ?: return DiscoveredTv(ip, "Samsung Smart TV", null, null)
                    val name = device["name"] as? String ?: "Samsung Smart TV"
                    val model = device["modelName"] as? String
                    val uuid = device["duid"] as? String ?: device["id"] as? String
                    return DiscoveredTv(ip, name, model, uuid)
                }
            }
        } catch (e: Exception) {
            // Ignore socket/connection timeout exceptions
        }
        return null
    }
}
