package com.ale.quickscore.features.rooms.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.features.rooms.data.datasources.remote.websocket.WebSocketManager
import com.ale.quickscore.features.rooms.domain.usecases.AddScoreUseCase
import com.ale.quickscore.features.rooms.domain.usecases.CreateRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.EndRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.GetRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.JoinRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.StartRoomUseCase
import com.ale.quickscore.features.rooms.presentation.screens.RoomUIState
import com.ale.quickscore.core.di.SessionManager
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
    private val wsManager: WebSocketManager,
    private val sessionManager: SessionManager  // ✅ agregado
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUIState())
    val uiState = _uiState.asStateFlow()

    private var currentRoomCode: String = ""

    fun getCurrentUserId(): Int = sessionManager.getUserId()  // ✅ expuesto

    fun initRoom(roomCode: String) {
        currentRoomCode = roomCode
        loadRoom(roomCode)
        connectWebSocket(roomCode)
    }

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

    fun startRoom(roomCode: String) = viewModelScope.launch {
        startRoomUseCase(roomCode)
    }

    fun endRoom(roomCode: String) = viewModelScope.launch {
        endRoomUseCase(roomCode)
    }

    fun addScore(roomCode: String, targetUserId: Int, delta: Int) = viewModelScope.launch {
        addScoreUseCase(roomCode, targetUserId, delta)
    }

    private fun loadRoom(roomCode: String) {
        viewModelScope.launch {
            getRoomUseCase(roomCode).fold(
                onSuccess = { room ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            room = room,
                            sessionStarted = room.status == "started"  // ✅ estado real
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun connectWebSocket(roomCode: String) {
        wsManager.connect(roomCode)

        wsManager.connectionState
            .onEach { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }.launchIn(viewModelScope)

        wsManager.onScoreUpdate()
            .onEach { loadRoom(roomCode) }
            .launchIn(viewModelScope)

        wsManager.onUserJoined()
            .onEach { loadRoom(roomCode) }   // ✅ recarga al unirse alguien
            .launchIn(viewModelScope)

        wsManager.onSessionStarted()
            .onEach { _uiState.update { it.copy(sessionStarted = true) } }
            .launchIn(viewModelScope)

        wsManager.onSessionEnded()
            .onEach { _uiState.update { it.copy(sessionEnded = true) } }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.disconnect()
    }
}