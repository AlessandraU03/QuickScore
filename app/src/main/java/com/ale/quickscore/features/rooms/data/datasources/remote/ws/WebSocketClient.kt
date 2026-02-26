package com.ale.quickscore.features.rooms.data.datasources.remote.websocket

import android.util.Log
import com.ale.quickscore.BuildConfig
import com.ale.quickscore.features.rooms.domain.entities.WsMessage
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _messages = MutableSharedFlow<WsMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<WsMessage> = _messages

    private val _connectionState = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState

    fun connect(roomCode: String, token: String) {
        // Aseguramos que reemplace https -> wss o http -> ws correctamente
        val wsBaseUrl = if (BuildConfig.BASE_URL.startsWith("https")) {
            BuildConfig.BASE_URL.replace("https", "wss")
        } else {
            BuildConfig.BASE_URL.replace("http", "ws")
        }

        val url = "${wsBaseUrl.trimEnd('/')}/ws?room=$roomCode&token=$token"
        
        Log.d("WebSocketClient", "Conectando a: $url")

        val request = Request.Builder().url(url).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocketClient", "Conexión abierta")
                _connectionState.tryEmit(true)
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocketClient", "Mensaje recibido: $text")
                try {
                    val message = gson.fromJson(text, WsMessage::class.java)
                    _messages.tryEmit(message)
                } catch (e: Exception) { 
                    Log.e("WebSocketClient", "Error parseando mensaje: ${e.message}")
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocketClient", "Falla de conexión: ${t.message}")
                _connectionState.tryEmit(false)
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocketClient", "Conexión cerrada: $reason")
                _connectionState.tryEmit(false)
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Usuario salió de la sala")
        webSocket = null
    }
}
