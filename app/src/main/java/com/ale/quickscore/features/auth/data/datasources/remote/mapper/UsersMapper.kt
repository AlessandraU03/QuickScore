package com.ale.quickscore.features.auth.data.datasources.remote.mapper

import com.ale.quickscore.features.auth.data.datasources.remote.models.AuthResponse
import com.ale.quickscore.features.auth.domain.entities.User

fun AuthResponse.toDomain(): User {
    return User(
        id = user_id ?: 0,
        name = name ?: "",
        email = email ?: "",
        role = role ?: "",
        token = token ?: ""
    )
}
