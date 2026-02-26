package com.ale.quickscore.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
data class HomeRoute(val isHost: Boolean)

@Serializable
data class RoomDetailRoute(
    val roomCode: String,
    val isHost: Boolean
)

@Serializable
data class LeaderboardRoute(val roomCode: String)