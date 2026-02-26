package com.ale.quickscore.features.rooms.presentation.screens

import com.ale.quickscore.features.rooms.domain.entities.Room

data class RoomUIState(
    val joinCode: String = "",
    val isLoading: Boolean = false,
    val room: Room? = null,
    val isConnected: Boolean = false,
    val sessionStarted: Boolean = false,
    val sessionEnded: Boolean = false,
    val error: String? = null
)
