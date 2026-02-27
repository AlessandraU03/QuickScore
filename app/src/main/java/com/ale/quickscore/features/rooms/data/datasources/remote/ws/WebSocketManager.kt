package com.ale.quickscore.features.rooms.data.datasources.remote.websocket

import com.ale.quickscore.core.di.SessionManager
import com.ale.quickscore.features.rooms.domain.entities.WsEvents
import com.ale.quickscore.features.rooms.domain.entities.WsMessage
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: WebSocketClient,
    private val sessionManager: SessionManager
) {
    val messages: SharedFlow<WsMessage> = client.messages
    val connectionState: SharedFlow<Boolean> = client.connectionState

    fun connect(roomCode: String) {
        val token = sessionManager.getToken() ?: return
        val name  = sessionManager.getUserName()
        client.connect(roomCode, token, name)
    }

    fun disconnect() = client.disconnect()

    // Filtros por evento (Deben coincidir con WsEvents.kt y tu Go)
    fun onScoreUpdate()    = messages.filter { it.event == WsEvents.SCORE_UPDATE }
    fun onSessionStarted() = messages.filter { it.event == WsEvents.SESSION_STARTED }
    fun onSessionEnded()   = messages.filter { it.event == WsEvents.SESSION_ENDED }
    fun onParticipantConnected()    = messages.filter { it.event == WsEvents.PARTICIPANT_CONNECTED }
    fun onParticipantDisconnected() = messages.filter { it.event == WsEvents.PARTICIPANT_DISCONNECTED }
    fun onOnlineList()              = messages.filter { it.event == WsEvents.ONLINE_LIST }
    fun onParticipantKicked()       = messages.filter { it.event == WsEvents.PARTICIPANT_KICKED }
    fun onNewQuestion()    = messages.filter { it.event == WsEvents.NEW_QUESTION }
    fun onQuestionClosed() = messages.filter { it.event == WsEvents.QUESTION_CLOSED }
    fun onAnswerCorrect()  = messages.filter { it.event == WsEvents.ANSWER_CORRECT }
}
