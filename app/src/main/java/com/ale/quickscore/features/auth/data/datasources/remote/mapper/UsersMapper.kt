package com.ale.quickscore.features.auth.data.datasources.remote.mapper

import com.ale.quickscore.features.auth.data.datasources.remote.models.AuthResponse
import com.ale.quickscore.features.auth.domain.entities.User

fun AuthResponse.toDomain(): User {
    return User(
        id = user?.id ?: 0,
        name = user?.name ?: "",
        email = user?.email ?: "",
        role = user?.role ?: "",
        token = token ?: ""
    )
}
