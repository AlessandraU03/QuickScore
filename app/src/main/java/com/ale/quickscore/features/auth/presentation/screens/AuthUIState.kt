package com.ale.quickscore.features.auth.presentation.screens

import com.ale.quickscore.features.auth.domain.entities.User

data class AuthUIState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)