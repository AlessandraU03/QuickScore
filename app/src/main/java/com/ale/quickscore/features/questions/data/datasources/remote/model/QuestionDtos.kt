package com.ale.quickscore.features.questions.data.datasources.remote.model

data class QuestionDto(
    val id: Int?,
    val room_id: Int?,
    val text: String?,
    val points: Int?,
    val status: String?
)

data class LaunchQuestionRequest(
    val text: String,
    val correct_answer: String,
    val points: Int
)

data class SubmitAnswerRequest(
    val question_id: Int,
    val answer: String
)

data class AnswerResultDto(
    val is_correct: Boolean?,
    val points_earned: Int?,
    val message: String?
)
