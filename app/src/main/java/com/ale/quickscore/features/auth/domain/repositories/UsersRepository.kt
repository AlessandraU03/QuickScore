package com.ale.quickscore.features.auth.domain.repositories

import com.ale.quickscore.features.auth.domain.entities.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, name: String, password: String, role: String): Result<User>
}