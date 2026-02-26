package com.ale.quickscore.features.auth.domain.entities

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,  // "host" | "participant"
    val token: String
)