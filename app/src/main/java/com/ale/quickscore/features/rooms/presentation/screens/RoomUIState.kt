package com.ale.quickscore.features.rooms.presentation.screens

import com.ale.quickscore.features.questions.domain.entities.Question
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.entities.Room

data class OnlineUser(
    val userId: Int,
    val name: String,
    val role: String
)

data class RoomUIState(
    val joinCode: String = "",
    val isLoading: Boolean = false,
    val room: Room? = null,
    val isConnected: Boolean = false,
    val sessionStarted: Boolean = false,
    val sessionEnded: Boolean = false,
    val error: String? = null,
    
    // Presencia en tiempo real
    val onlineUsers: List<OnlineUser> = emptyList(),
    
    // Ranking
    val ranking: List<RankingItem> = emptyList(),
    
    // Pregunta activa
    val activeQuestion: Question? = null,
    val isAnswering: Boolean = false,

    // Resultado de la última respuesta enviada
    val lastAnswerCorrect: Boolean? = null,
    val lastAnswerPoints: Int = 0,
    val lastAnswerMessage: String = "",
    
    // Kick dialog
    val showKickDialog: Boolean = false,
    val kickTargetId: Int? = null,
    val kickTargetName: String = ""
)
