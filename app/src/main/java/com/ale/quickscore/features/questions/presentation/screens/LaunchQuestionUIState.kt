package com.ale.quickscore.features.questions.presentation.screens

data class LaunchQuestionUIState(
    val text: String          = "",
    val correctAnswer: String = "",
    val points: String        = "10",
    val isLoading: Boolean    = false,
    val success: Boolean      = false,
    val error: String?        = null
)