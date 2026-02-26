package com.ale.quickscore.features.rooms.data.datasources.remote.websocket

import android.util.Log
import com.ale.quickscore.BuildConfig
import com.ale.quickscore.features.rooms.domain.entities.WsMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val gson = Gson()
    
    // Scope para manejar la lógica de reconexión fuera del ciclo de vida de la UI
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var reconnectJob: Job? = null
    private var currentUrl: String? = null
    private var isUserClosing = false

    private val _messages = MutableSharedFlow<WsMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<WsMessage> = _messages

    private val _connectionState = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState

    fun connect(roomCode: String, token: String, name: String) {
        isUserClosing = false
        reconnectJob?.cancel()
        
        val wsBase = if (BuildConfig.BASE_URL.startsWith("https"))
            BuildConfig.BASE_URL.replace("https", "wss")
        else
            BuildConfig.BASE_URL.replace("http", "ws")

        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
        currentUrl = "${wsBase.trimEnd('/')}/ws?room=$roomCode&token=$token&name=$encodedName"

        doConnect()
    }

    private fun doConnect() {
        val url = currentUrl ?: return
        Log.d("WebSocketClient", "Intentando conectar a: $url")

        val request = Request.Builder().url(url).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocketClient", "Conexión establecida exitosamente")
                _connectionState.tryEmit(true)
                reconnectJob?.cancel() // Cancelamos cualquier reintento si ya conectamos
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = gson.fromJson(text, WsMessage::class.java)
                    _messages.tryEmit(message)
                } catch (e: Exception) {
                    Log.e("WebSocketClient", "Error al parsear mensaje: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocketClient", "Error de conexión: ${t.message}")
                _connectionState.tryEmit(false)
                attemptReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocketClient", "Conexión cerrada: $reason")
                _connectionState.tryEmit(false)
                if (!isUserClosing) {
                    attemptReconnect()
                }
            }
        })
    }

    private fun attemptReconnect() {
        if (isUserClosing) return
        
        // Evitamos solapar múltiples trabajos de reconexión
        if (reconnectJob?.isActive == true) return

        reconnectJob = scope.launch {
            Log.d("WebSocketClient", "Reconectando en 5 segundos...")
            delay(5000)
            doConnect()
        }
    }

    fun disconnect() {
        isUserClosing = true
        reconnectJob?.cancel()
        webSocket?.close(1000, "Cierre manual por el usuario")
        webSocket = null
        currentUrl = null
    }
}
