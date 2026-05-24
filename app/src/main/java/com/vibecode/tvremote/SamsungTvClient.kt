package com.vibecode.tvremote

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class SamsungTvClient(
    private val appName: String = "VibeRemote",
    private val onStateChanged: (State) -> Unit,
    private val onTokenReceived: (String) -> Unit
) {
    companion object {
        private const val TAG = "SamsungTvClient"
        private const val PORT_SECURE = 8002
    }

    enum class State {
        DISCONNECTED,
        CONNECTING,
        PAIRING, // Waiting for user to click Allow on TV
        CONNECTED,
        ERROR
    }

    private val gson = Gson()
    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null
    var currentState: State = State.DISCONNECTED
        private set(value) {
            field = value
            onStateChanged(value)
        }

    init {
        client = createUnsafeOkHttpClient()
    }

    fun connect(ip: String, token: String? = null) {
        if (currentState == State.CONNECTING || currentState == State.CONNECTED) {
            disconnect()
        }

        currentState = State.CONNECTING
        val encodedAppName = Base64.encodeToString(appName.toByteArray(), Base64.NO_WRAP)
        val url = StringBuilder("wss://$ip:$PORT_SECURE/api/v2/channels/samsung.remote.control?name=$encodedAppName")
        if (!token.isNullOrEmpty()) {
            url.append("&token=$token")
        }

        val request = Request.Builder()
            .url(url.toString())
            .build()

        webSocket = client?.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket opened")
                currentState = State.PAIRING
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
                try {
                    val map = gson.fromJson(text, Map::class.java)
                    val event = map["event"] as? String
                    if (event == "ms.channel.connect") {
                        val data = map["data"] as? Map<*, *>
                        val receivedToken = data?.get("token") as? String
                        if (receivedToken != null) {
                            onTokenReceived(receivedToken)
                        }
                        currentState = State.CONNECTED
                    } else if (event == "ms.channel.unauthorized") {
                        currentState = State.ERROR
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code / $reason")
                currentState = State.DISCONNECTED
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed")
                currentState = State.DISCONNECTED
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}", t)
                currentState = State.ERROR
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "App request disconnect")
        webSocket = null
        currentState = State.DISCONNECTED
    }

    fun sendKey(key: String) {
        if (currentState != State.CONNECTED) {
            Log.w(TAG, "Cannot send key: Client not connected")
            return
        }

        val payload = """
            {
                "method": "ms.remote.control",
                "params": {
                    "Cmd": "Click",
                    "DataOfCmd": "$key",
                    "Option": "false",
                    "TypeOfRemote": "SendRemoteKey"
                }
            }
        """.trimIndent()
        webSocket?.send(payload)
    }

    fun sendText(text: String) {
        if (currentState != State.CONNECTED) return
        val encodedText = Base64.encodeToString(text.toByteArray(), Base64.NO_WRAP)
        val payload = """
            {
                "method": "ms.remote.control",
                "params": {
                    "Cmd": "$encodedText",
                    "DataOfCmd": "base64",
                    "Option": "false",
                    "TypeOfRemote": "SendInputString"
                }
            }
        """.trimIndent()
        webSocket?.send(payload)
    }

    fun launchApp(appId: String) {
        if (currentState != State.CONNECTED) return
        val payload = """
            {
                "method": "ms.channel.emit",
                "params": {
                    "event": "ed.apps.launch",
                    "to": "host",
                    "data": {
                        "action_type": "DEEP_LINK",
                        "appId": "$appId"
                    }
                }
            }
        """.trimIndent()
        webSocket?.send(payload)
    }

    private fun createUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
