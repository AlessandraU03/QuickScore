package com.ale.quickscore.features.questions.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.features.questions.domain.usecases.LaunchQuestionUseCase
import com.ale.quickscore.features.questions.presentation.screens.LaunchQuestionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val launchQuestionUseCase: LaunchQuestionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LaunchQuestionUIState())
    val uiState = _uiState.asStateFlow()

    fun onTextChange(value: String)          = _uiState.update { it.copy(text = value, error = null) }
    fun onCorrectAnswerChange(value: String) = _uiState.update { it.copy(correctAnswer = value, error = null) }
    fun onPointsChange(value: String)        = _uiState.update { it.copy(points = value) }

    fun launchQuestion(roomCode: String) = viewModelScope.launch {
        val state = _uiState.value
        if (state.text.isBlank() || state.correctAnswer.isBlank()) {
            _uiState.update { it.copy(error = "Completa la pregunta y la respuesta correcta") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        launchQuestionUseCase(
            roomCode      = roomCode,
            text          = state.text,
            correctAnswer = state.correctAnswer,
            points        = state.points.toIntOrNull() ?: 10
        ).fold(
            onSuccess = { _uiState.update { it.copy(isLoading = false, success = true) } },
            onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        )
    }

    fun resetForm() = _uiState.update { LaunchQuestionUIState() }
}
