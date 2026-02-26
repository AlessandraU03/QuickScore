package com.ale.quickscore.features.rooms.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.usecases.GetRankingUseCase
import com.ale.quickscore.features.rooms.presentation.screens.LeaderboardUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUIState())
    val uiState = _uiState.asStateFlow()

    fun loadRanking(roomCode: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, roomCode = roomCode) }
        getRankingUseCase(roomCode).fold(
            onSuccess = { ranking ->
                _uiState.update { it.copy(isLoading = false, ranking = ranking) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        )
    }
}
