package com.ale.quickscore.features.rooms.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.core.di.SessionManager

import com.ale.quickscore.features.rooms.data.datasources.remote.websocket.WebSocketManager
import com.ale.quickscore.features.rooms.domain.usecases.AddScoreUseCase
import com.ale.quickscore.features.rooms.domain.usecases.CreateRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.EndRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.GetRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.JoinRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.StartRoomUseCase
import com.ale.quickscore.features.rooms.presentation.screens.OnlineUser
import com.ale.quickscore.features.rooms.presentation.screens.RoomUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val createRoomUseCase: CreateRoomUseCase,
    private val getRoomUseCase: GetRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val startRoomUseCase: StartRoomUseCase,
    private val endRoomUseCase: EndRoomUseCase,
    private val addScoreUseCase: AddScoreUseCase,
    // Preguntas

    private val wsManager: WebSocketManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUIState())
    val uiState = _uiState.asStateFlow()

    private var currentRoomCode: String = ""

    fun getCurrentUserId(): Int = sessionManager.getUserId()

    fun initRoom(roomCode: String) {
        currentRoomCode = roomCode
        loadRoom(roomCode)
        connectWebSocket(roomCode)

    }

    // ── Sala ────────────────────────────────────────────────

    fun createRoom() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        createRoomUseCase().fold(
            onSuccess = { code ->
                currentRoomCode = code
                loadRoom(code)
            },
            onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        )
    }

    fun joinRoom(code: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        joinRoomUseCase(code).fold(
            onSuccess = { initRoom(code) },
            onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        )
    }

    fun startRoom(roomCode: String) = viewModelScope.launch { startRoomUseCase(roomCode) }

    fun endRoom(roomCode: String) = viewModelScope.launch { endRoomUseCase(roomCode) }

    fun addScore(roomCode: String, targetUserId: Int, delta: Int) = viewModelScope.launch {
        addScoreUseCase(roomCode, targetUserId, delta)
    }

    // ── Preguntas ────────────────────────────────────────────

    fun clearAnswerResult() {
        _uiState.update { it.copy(lastAnswerCorrect = null, lastAnswerPoints = 0, lastAnswerMessage = "") }
    }

    // ── Internos ─────────────────────────────────────────────

    private fun loadRoom(roomCode: String) {
        viewModelScope.launch {
            getRoomUseCase(roomCode).fold(
                onSuccess = { room ->
                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            room           = room,
                            sessionStarted = room.status == "active"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }


    private fun parseOnlineUser(payload: Map<String, Any>?): OnlineUser? {
        payload ?: return null
        return OnlineUser(
            userId = (payload["user_id"] as? Double)?.toInt() ?: return null,
            name   = payload["name"] as? String ?: "",
            role   = payload["role"] as? String ?: ""
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun connectWebSocket(roomCode: String) {
        wsManager.connect(roomCode)

        wsManager.connectionState
            .onEach { connected -> _uiState.update { it.copy(isConnected = connected) } }
            .launchIn(viewModelScope)

        // Score actualizado → recargar sala
        wsManager.onScoreUpdate()
            .onEach { loadRoom(roomCode) }
            .launchIn(viewModelScope)

        // Sesión
        wsManager.onSessionStarted()
            .onEach { _uiState.update { it.copy(sessionStarted = true) } }
            .launchIn(viewModelScope)

        wsManager.onSessionEnded()
            .onEach { _uiState.update { it.copy(sessionEnded = true) } }
            .launchIn(viewModelScope)

        // Presencia — alguien se conectó
        wsManager.onParticipantConnected()
            .onEach { msg ->
                parseOnlineUser(msg.payload)?.let { user ->
                    _uiState.update { state ->
                        val updated = state.onlineUsers.filterNot { it.userId == user.userId } + user
                        state.copy(onlineUsers = updated)
                    }
                }
            }.launchIn(viewModelScope)

        // Presencia — alguien se desconectó
        wsManager.onParticipantDisconnected()
            .onEach { msg ->
                val userId = (msg.payload?.get("user_id") as? Double)?.toInt()
                userId?.let { id ->
                    _uiState.update { state ->
                        state.copy(onlineUsers = state.onlineUsers.filterNot { it.userId == id })
                    }
                }
            }.launchIn(viewModelScope)

        // Lista inicial de conectados al entrar
        wsManager.onOnlineList()
            .onEach { msg ->
                val list = (msg.payload?.get("payload") as? List<Map<String, Any>>)
                    ?.mapNotNull { parseOnlineUser(it) } ?: emptyList()
                _uiState.update { it.copy(onlineUsers = list) }
            }.launchIn(viewModelScope)

        // Kick — si me expulsaron a mí
        wsManager.onParticipantKicked()
            .onEach { msg ->
                val kickedId = (msg.payload?.get("user_id") as? Double)?.toInt()
                if (kickedId == sessionManager.getUserId()) {
                    _uiState.update { it.copy(sessionEnded = true, error = "Fuiste expulsado de la sala") }
                } else {
                    _uiState.update { state ->
                        state.copy(onlineUsers = state.onlineUsers.filterNot { it.userId == kickedId })
                    }
                }
            }.launchIn(viewModelScope)


        // Pregunta cerrada


        // Alguien respondió correcto → recargar ranking
        wsManager.onAnswerCorrect()
            .onEach { loadRoom(roomCode) }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.disconnect()
    }
}
