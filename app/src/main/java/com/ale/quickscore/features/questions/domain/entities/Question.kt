package com.ale.quickscore.features.questions.domain.entities

data class Question(
    val id: Int,
    val roomId: Int,
    val text: String,
    val points: Int,
    val status: String  // "open" | "closed"
)

data class AnswerResult(
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val message: String
)
