package com.example.data.network

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import okhttp3.HttpUrl.Companion.toHttpUrl

object RemoteNetworkClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    fun sendCommand(
        ip: String,
        port: Int,
        type: String, // "mac", "windows", "android_tv"
        action: String, // "volume_up", "left_click", "ok", etc.
        extraParams: Map<String, String> = emptyMap(),
        onResult: (Boolean, String) -> Unit
    ) {
        val host = if (ip.isBlank()) "127.0.0.1" else ip
        val cleanPort = if (port <= 0 || port > 65535) 8080 else port

        // Ensure the host is properly formatted as http://
        val baseUrl = if (host.startsWith("http://") || host.startsWith("https://")) {
            "$host:$cleanPort/api/control"
        } else {
            "http://$host:$cleanPort/api/control"
        }

        val urlBuilder = try {
            baseUrl.toHttpUrl().newBuilder()
        } catch (e: Exception) {
            onResult(false, "Malformed IP Address")
            return
        }

        urlBuilder.addQueryParameter("device_type", type)
        urlBuilder.addQueryParameter("action", action)

        extraParams.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("RemoteNetworkClient", "Failed to send $action to $host:$cleanPort", e)
                onResult(false, "Timeout/Offline")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onResult(true, "Success (${response.code})")
                } else {
                    onResult(false, "Error ${response.code}")
                }
                response.close()
            }
        })
    }

    fun sendWakeOnLan(macAddress: String, ipAddress: String, onResult: (Boolean, String) -> Unit) {
        Thread {
            try {
                val cleanMac = macAddress.replace(":", "").replace("-", "")
                if (cleanMac.length != 12) {
                    onResult(false, "Invalid MAC format")
                    return@Thread
                }
                val macBytes = ByteArray(6)
                for (i in 0..5) {
                    macBytes[i] = cleanMac.substring(i * 2, i * 2 + 2).toInt(16).toByte()
                }

                val bytes = ByteArray(6 + 16 * macBytes.size)
                for (i in 0..5) {
                    bytes[i] = 0xff.toByte()
                }
                for (i in 1..16) {
                    System.arraycopy(macBytes, 0, bytes, 6 + i * macBytes.size, macBytes.size)
                }

                val broadcastIp = if (ipAddress.contains(".")) {
                    val parts = ipAddress.split(".")
                    if (parts.size == 4) {
                        "${parts[0]}.${parts[1]}.${parts[2]}.255"
                    } else "255.255.255.255"
                } else "255.255.255.255"

                val address = InetAddress.getByName(broadcastIp)
                val packet = DatagramPacket(bytes, bytes.size, address, 9)
                val socket = DatagramSocket()
                socket.broadcast = true
                socket.send(packet)
                socket.close()

                onResult(true, "Sent to $broadcastIp")
            } catch (e: Exception) {
                Log.e("RemoteNetworkClient", "WoL error", e)
                onResult(false, e.localizedMessage ?: "WoL Failed")
            }
        }.start()
    }
}
