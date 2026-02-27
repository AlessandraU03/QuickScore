package com.ale.quickscore.features.rooms.domain.entities

data class Room(
    val code: String,
    val hostId: Int,
    val status: String,
    val participants: List<Participant>
)



