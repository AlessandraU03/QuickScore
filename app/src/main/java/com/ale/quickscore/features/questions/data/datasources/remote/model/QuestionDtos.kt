package com.ale.quickscore.features.questions.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("room_id") val roomId: Int?,
    @SerializedName("text") val text: String?,
    @SerializedName("points") val points: Int?,
    @SerializedName("status") val status: String?
)

data class LaunchQuestionRequest(
    @SerializedName("text") val text: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("points") val points: Int
)

data class SubmitAnswerRequest(
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("answer") val answer: String
)

data class AnswerResultDto(
    @SerializedName("is_correct") val isCorrect: Boolean?,
    @SerializedName("points_earned") val pointsEarned: Int?,
    @SerializedName("message") val message: String?
)
