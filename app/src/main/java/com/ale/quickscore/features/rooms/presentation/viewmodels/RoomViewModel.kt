package com.ale.quickscore.features.rooms.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.core.data.local.dao.AppStateDao
import com.ale.quickscore.core.data.local.entities.AppStateEntity
import com.ale.quickscore.core.di.SessionManager
import com.ale.quickscore.features.questions.domain.entities.Question
import com.ale.quickscore.features.questions.domain.usecases.CloseQuestionUseCase
import com.ale.quickscore.features.questions.domain.usecases.GetCurrentQuestionUseCase
import com.ale.quickscore.features.questions.domain.usecases.LaunchQuestionUseCase
import com.ale.quickscore.features.questions.domain.usecases.SubmitAnswerUseCase
import com.ale.quickscore.features.rooms.data.datasources.remote.websocket.WebSocketManager
import com.ale.quickscore.features.rooms.domain.usecases.AddScoreUseCase
import com.ale.quickscore.features.rooms.domain.usecases.CreateRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.EndRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.GetCurrentRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.GetRankingUseCase
import com.ale.quickscore.features.rooms.domain.usecases.GetRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.JoinRoomUseCase
import com.ale.quickscore.features.rooms.domain.usecases.StartRoomUseCase
import com.ale.quickscore.features.rooms.presentation.screens.OnlineUser
import com.ale.quickscore.features.rooms.presentation.screens.RoomUIState
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val getCurrentRoomUseCase: GetCurrentRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val startRoomUseCase: StartRoomUseCase,
    private val endRoomUseCase: EndRoomUseCase,
    private val addScoreUseCase: AddScoreUseCase,
    private val getRankingUseCase: GetRankingUseCase,
    private val launchQuestionUseCase: LaunchQuestionUseCase,
    private val getCurrentQuestionUseCase: GetCurrentQuestionUseCase,
    private val closeQuestionUseCase: CloseQuestionUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val wsManager: WebSocketManager,
    private val sessionManager: SessionManager,
    private val appStateDao: AppStateDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUIState())
    val uiState = _uiState.asStateFlow()

    private var currentRoomCode: String = ""

    fun getCurrentUserId(): Int = sessionManager.getUserId()

    // ── Input ────────────────────────────────────────────────

    fun onInputCodeChange(code: String) {
        _uiState.update { it.copy(inputCode = code.uppercase()) }
    }

    fun onCurrentAnswerChange(answer: String) {
        _uiState.update { it.copy(currentAnswer = answer) }
    }

    // ── UI Control ───────────────────────────────────────────

    fun toggleLaunchSheet(show: Boolean) {
        if (show && !_uiState.value.sessionStarted) {
            _uiState.update { it.copy(error = "Primero debes iniciar la sesión para lanzar preguntas") }
            return
        }
        _uiState.update { it.copy(showLaunchSheet = show, error = null) }
    }

    // ── Sala ────────────────────────────────────────────────

    fun checkLastActiveRoom() = viewModelScope.launch {
        getCurrentRoomUseCase().fold(
            onSuccess = { room ->
                initRoom(room.code)
            },
            onFailure = {
                appStateDao.getAppState()?.let { state ->
                    if (state.isInRoom && state.currentRoomCode != null) {
                        if (currentRoomCode != state.currentRoomCode) {
                            initRoom(state.currentRoomCode)
                        }
                    }
                }
            }
        )
    }

    fun createRoom(onSuccess: (String) -> Unit) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        createRoomUseCase().fold(
            onSuccess = { code ->
                initRoom(code)
                onSuccess(code)
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        )
    }

    fun joinRoom(code: String = _uiState.value.inputCode, onSuccess: (String) -> Unit = {}) = viewModelScope.launch {
        if (code.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa un código válido") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        joinRoomUseCase(code).fold(
            onSuccess = {
                initRoom(code)
                onSuccess(code)
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        )
    }

    fun initRoom(roomCode: String) {
        if (currentRoomCode == roomCode && _uiState.value.isConnected) return
        
        currentRoomCode = roomCode
        loadRoom(roomCode)
        connectWebSocket(roomCode)
        loadCurrentQuestion(roomCode)
        loadRanking(roomCode)
        
        saveAppState(roomCode, isInRoom = true)
    }
    
    private fun saveAppState(roomCode: String, isInRoom: Boolean) {
        viewModelScope.launch {
            appStateDao.saveAppState(
                AppStateEntity(
                    currentRoomCode = roomCode,
                    isInRoom = isInRoom,
                    isHost = sessionManager.isHost()
                )
            )
        }
    }

    fun startRoom(roomCode: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        startRoomUseCase(roomCode).fold(
            onSuccess = {
                _uiState.update { it.copy(isLoading = false, sessionStarted = true) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        )
    }

    fun endRoom(roomCode: String) = viewModelScope.launch {
        endRoomUseCase(roomCode).onFailure { e ->
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun addScore(roomCode: String, targetUserId: Int, delta: Int) = viewModelScope.launch {
        addScoreUseCase(roomCode, targetUserId, delta).onSuccess {
            // No es necesario llamar a loadRoom aquí, el WS debería enviar la actualización
        }
    }

    // ── Kick dialog ──────────────────────────────────────────

    fun onKickRequest(userId: Int, name: String) {
        _uiState.update { it.copy(showKickDialog = true, kickTargetId = userId, kickTargetName = name) }
    }

    fun onKickDismiss() {
        _uiState.update { it.copy(showKickDialog = false, kickTargetId = null, kickTargetName = "") }
    }

    fun confirmKick() = viewModelScope.launch {
        // Implementación de kick si el backend lo soporta vía API o WS
        onKickDismiss()
    }

    // ── Preguntas ─────────────────────────────────────────────

    fun launchQuestion(text: String, correctAnswer: String, points: Int) = viewModelScope.launch {
        if (!_uiState.value.sessionStarted) {
            _uiState.update { it.copy(error = "Inicia la sesión antes de lanzar una pregunta") }
            return@launch
        }
        
        launchQuestionUseCase(currentRoomCode, text, correctAnswer, points).fold(
            onSuccess = { q -> 
                _uiState.update { it.copy(activeQuestion = q, showLaunchSheet = false, error = null) } 
            },
            onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
        )
    }

    fun closeQuestion() = viewModelScope.launch {
        val qId = _uiState.value.activeQuestion?.id ?: return@launch
        closeQuestionUseCase(currentRoomCode, qId).fold(
            onSuccess = { _uiState.update { it.copy(activeQuestion = null) } },
            onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
        )
    }

    fun submitAnswer() = viewModelScope.launch {
        val qId = _uiState.value.activeQuestion?.id ?: return@launch
        val answer = _uiState.value.currentAnswer
        if (answer.isBlank()) return@launch

        _uiState.update { it.copy(isAnswering = true) }
        submitAnswerUseCase(currentRoomCode, qId, answer).fold(
            onSuccess = { result ->
                _uiState.update {
                    it.copy(
                        isAnswering       = false,
                        currentAnswer     = "",
                        lastAnswerCorrect = result.isCorrect,
                        lastAnswerPoints  = result.pointsEarned,
                        lastAnswerMessage = result.message
                    )
                }
                // Si es correcta, el WS notificará a todos y disparará la actualización del ranking
                delay(3000)
                _uiState.update { it.copy(lastAnswerCorrect = null) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isAnswering = false, error = e.message) }
            }
        )
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    // ── Internos ──────────────────────────────────────────────

    private fun loadRoom(roomCode: String) = viewModelScope.launch {
        getRoomUseCase(roomCode).fold(
            onSuccess = { room ->
                _uiState.update {
                    it.copy(
                        isLoading      = false,
                        room           = room,
                        sessionStarted = room.status == "active",
                        sessionEnded   = room.status == "finished"
                    )
                }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        )
    }

    private fun loadRanking(roomCode: String) = viewModelScope.launch {
        getRankingUseCase(roomCode).fold(
            onSuccess = { ranking -> _uiState.update { it.copy(ranking = ranking) } },
            onFailure = { }
        )
    }

    private fun loadCurrentQuestion(roomCode: String) = viewModelScope.launch {
        getCurrentQuestionUseCase(roomCode).fold(
            onSuccess = { q -> _uiState.update { it.copy(activeQuestion = q) } },
            onFailure = { _uiState.update { it.copy(activeQuestion = null) } }
        )
    }

    private fun parseOnlineUser(element: JsonElement?): OnlineUser? {
        val obj = element?.takeIf { it.isJsonObject }?.asJsonObject ?: return null
        return OnlineUser(
            userId = obj.get("user_id")?.asInt ?: return null,
            name   = obj.get("name")?.asString ?: "",
            role   = obj.get("role")?.asString ?: ""
        )
    }

    private fun connectWebSocket(roomCode: String) {
        wsManager.connect(roomCode)

        wsManager.connectionState
            .onEach { connected -> _uiState.update { it.copy(isConnected = connected) } }
            .launchIn(viewModelScope)

        wsManager.onScoreUpdate()
            .onEach {
                loadRanking(roomCode)
                loadRoom(roomCode)
            }.launchIn(viewModelScope)

        wsManager.onSessionStarted()
            .onEach { _uiState.update { it.copy(sessionStarted = true, sessionEnded = false) } }
            .launchIn(viewModelScope)

        wsManager.onSessionEnded()
            .onEach { _uiState.update { it.copy(sessionEnded = true, sessionStarted = false) } }
            .launchIn(viewModelScope)

        wsManager.onParticipantConnected()
            .onEach { msg ->
                parseOnlineUser(msg.payload)?.let { user ->
                    _uiState.update { state ->
                        val updated = state.onlineUsers.filterNot { it.userId == user.userId } + user
                        state.copy(onlineUsers = updated)
                    }
                }
            }.launchIn(viewModelScope)

        wsManager.onParticipantDisconnected()
            .onEach { msg ->
                val userId = msg.payload?.takeIf { it.isJsonObject }?.asJsonObject?.get("user_id")?.asInt
                _uiState.update { state ->
                    state.copy(onlineUsers = state.onlineUsers.filterNot { it.userId == userId })
                }
            }.launchIn(viewModelScope)

        wsManager.onOnlineList()
            .onEach { msg ->
                val list = msg.payload?.takeIf { it.isJsonArray }?.asJsonArray
                    ?.mapNotNull { parseOnlineUser(it) } ?: emptyList()
                _uiState.update { it.copy(onlineUsers = list) }
            }.launchIn(viewModelScope)

        wsManager.onNewQuestion()
            .onEach { msg ->
                val p = msg.payload?.takeIf { it.isJsonObject }?.asJsonObject
                val q = Question(
                    id     = p?.get("id")?.asInt ?: 0,
                    roomId = p?.get("room_id")?.asInt ?: 0,
                    text   = p?.get("text")?.asString ?: "",
                    points = p?.get("points")?.asInt ?: 0,
                    status = "open"
                )
                _uiState.update { it.copy(activeQuestion = q, currentAnswer = "") }
            }.launchIn(viewModelScope)

        wsManager.onQuestionClosed()
            .onEach { _uiState.update { it.copy(activeQuestion = null) } }
            .launchIn(viewModelScope)
            
        wsManager.onAnswerCorrect()
            .onEach { 
                loadRanking(roomCode)
            }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.disconnect()
        currentRoomCode = ""
    }
}
