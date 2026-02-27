package com.ale.quickscore.features.questions.data.repositories

import com.ale.quickscore.features.questions.data.datasources.remote.api.QuestionsApi
import com.ale.quickscore.features.questions.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.questions.data.datasources.remote.model.LaunchQuestionRequest
import com.ale.quickscore.features.questions.data.datasources.remote.model.SubmitAnswerRequest
import com.ale.quickscore.features.questions.domain.entities.AnswerResult
import com.ale.quickscore.features.questions.domain.entities.Question
import com.ale.quickscore.features.questions.domain.repositories.QuestionsRepository
import org.json.JSONObject
import javax.inject.Inject

class QuestionsRepositoryImpl @Inject constructor(
    private val api: QuestionsApi
) : QuestionsRepository {

    override suspend fun launchQuestion(
        roomCode: String,
        text: String,
        correctAnswer: String,
        points: Int
    ): Result<Question> = runCatching {
        val res = api.launchQuestion(roomCode, LaunchQuestionRequest(text, correctAnswer, points))
        if (res.isSuccessful) {
            res.body()?.toDomain() ?: throw Exception("Sin respuesta del servidor")
        } else {
            val errorMsg = parseError(res.errorBody()?.string()) ?: "Error ${res.code()}"
            throw Exception(errorMsg)
        }
    }

    override suspend fun getCurrentQuestion(roomCode: String): Result<Question?> = runCatching {
        val res = api.getCurrentQuestion(roomCode)
        when (res.code()) {
            200  -> res.body()?.toDomain()
            204  -> null
            else -> {
                val errorMsg = parseError(res.errorBody()?.string()) ?: "Error ${res.code()}"
                throw Exception(errorMsg)
            }
        }
    }

    override suspend fun closeQuestion(roomCode: String, questionId: Int): Result<Unit> = runCatching {
        val res = api.closeQuestion(roomCode, questionId)
        if (!res.isSuccessful) {
            val errorMsg = parseError(res.errorBody()?.string()) ?: "Error ${res.code()}"
            throw Exception(errorMsg)
        }
    }

    override suspend fun submitAnswer(
        roomCode: String,
        questionId: Int,
        answer: String
    ): Result<AnswerResult> = runCatching {
        val res = api.submitAnswer(roomCode, SubmitAnswerRequest(questionId, answer))
        if (res.isSuccessful) {
            res.body()?.toDomain() ?: throw Exception("Sin respuesta")
        } else {
            val errorMsg = parseError(res.errorBody()?.string()) ?: "Error ${res.code()}"
            throw Exception(errorMsg)
        }
    }

    private fun parseError(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val json = JSONObject(errorBody)
            json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optString("error").takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
