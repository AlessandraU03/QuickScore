package com.ale.quickscore.features.auth.presentation.screens

import com.ale.quickscore.features.auth.domain.entities.User

data class AuthUIState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val role: String = "participant",
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val acceptTerms: Boolean = false
)
