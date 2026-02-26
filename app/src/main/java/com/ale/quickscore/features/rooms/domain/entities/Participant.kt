package com.ale.quickscore.features.rooms.domain.entities

data class Participant(
    val userId: Int,
    val name: String,
    val score: Int
)