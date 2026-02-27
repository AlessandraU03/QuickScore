package com.ale.quickscore.features.rooms.domain.entities

data class RankingItem(
    val userId: Int,
    val name: String,
    val score: Int
)