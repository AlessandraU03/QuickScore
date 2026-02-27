package com.ale.quickscore.features.questions.data.datasources.remote.api

import com.ale.quickscore.features.questions.data.datasources.remote.model.AnswerResultDto
import com.ale.quickscore.features.questions.data.datasources.remote.model.LaunchQuestionRequest
import com.ale.quickscore.features.questions.data.datasources.remote.model.QuestionDto
import com.ale.quickscore.features.questions.data.datasources.remote.model.SubmitAnswerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface QuestionsApi {

    @POST("rooms/{code}/questions")
    suspend fun launchQuestion(
        @Path("code") roomCode: String,
        @Body body: LaunchQuestionRequest
    ): Response<QuestionDto>

    @GET("rooms/{code}/questions/current")
    suspend fun getCurrentQuestion(
        @Path("code") roomCode: String
    ): Response<QuestionDto>

    @PATCH("rooms/{code}/questions/{question_id}/close")
    suspend fun closeQuestion(
        @Path("code") roomCode: String,
        @Path("question_id") questionId: Int
    ): Response<Map<String, String>>

    @POST("rooms/{code}/answer")
    suspend fun submitAnswer(
        @Path("code") roomCode: String,
        @Body body: SubmitAnswerRequest
    ): Response<AnswerResultDto>
}
