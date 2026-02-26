package com.ale.quickscore.features.rooms.data.datasources.remote.websocket

import com.ale.quickscore.core.di.TokenProvider
import com.ale.quickscore.features.rooms.domain.entities.WsEvents
import com.ale.quickscore.features.rooms.domain.entities.WsMessage
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: WebSocketClient,
    private val tokenProvider: TokenProvider
) {
    val messages: SharedFlow<WsMessage> = client.messages
    val connectionState: SharedFlow<Boolean> = client.connectionState

    fun connect(roomCode: String) {
        val token = tokenProvider.getToken() ?: return
        client.connect(roomCode, token)
    }

    fun disconnect() = client.disconnect()

    fun onScoreUpdate()    = messages.filter { it.event == WsEvents.SCORE_UPDATE }
    fun onSessionStarted() = messages.filter { it.event == WsEvents.SESSION_STARTED }
    fun onSessionEnded()   = messages.filter { it.event == WsEvents.SESSION_ENDED }
    fun onUserJoined()     = messages.filter { it.event == WsEvents.USER_JOINED }
}