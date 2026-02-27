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
            val errorBody = res.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody ?: "").getString("error")
            } catch (e: Exception) {
                when (res.code()) {
                    403 -> "Solo el host puede lanzar preguntas"
                    400 -> "La sesión debe estar activa o datos inválidos"
                    else -> "Error ${res.code()}: ${res.message()}"
                }
            }
            throw Exception(errorMessage)
        }
    }

    override suspend fun getCurrentQuestion(roomCode: String): Result<Question?> = runCatching {
        val res = api.getCurrentQuestion(roomCode)
        if (res.isSuccessful) {
            res.body()?.toDomain()
        } else if (res.code() == 204) {
            null
        } else {
            throw Exception("Error ${res.code()}")
        }
    }

    override suspend fun closeQuestion(roomCode: String, questionId: Int): Result<Unit> = runCatching {
        val res = api.closeQuestion(roomCode, questionId)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
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
            val errorBody = res.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody ?: "").getString("error")
            } catch (e: Exception) {
                "Error ${res.code()}"
            }
            throw Exception(errorMessage)
        }
    }
}
