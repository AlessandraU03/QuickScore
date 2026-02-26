package com.ale.quickscore.features.rooms.presentation.screens

import com.ale.quickscore.features.rooms.domain.entities.RankingItem

data class LeaderboardUIState(
    val isLoading: Boolean         = false,
    val ranking: List<RankingItem> = emptyList(),
    val roomCode: String           = "",
    val error: String?             = null
)
