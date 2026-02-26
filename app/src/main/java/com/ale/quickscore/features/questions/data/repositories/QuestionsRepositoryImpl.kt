package com.ale.quickscore.features.questions.data.repositories

import com.ale.quickscore.features.questions.data.datasources.remote.api.QuestionsApi
import com.ale.quickscore.features.questions.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.questions.data.datasources.remote.model.LaunchQuestionRequest
import com.ale.quickscore.features.questions.data.datasources.remote.model.SubmitAnswerRequest
import com.ale.quickscore.features.questions.domain.entities.AnswerResult
import com.ale.quickscore.features.questions.domain.entities.Question
import com.ale.quickscore.features.questions.domain.repositories.QuestionsRepository
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
        when (res.code()) {
            201, 200 -> res.body()?.toDomain() ?: throw Exception("Sin respuesta del servidor")
            403 -> throw Exception("Solo el host puede lanzar preguntas")
            400 -> throw Exception("La sesión debe estar activa")
            else -> throw Exception("Error ${res.code()}")
        }
    }

    override suspend fun getCurrentQuestion(roomCode: String): Result<Question?> = runCatching {
        val res = api.getCurrentQuestion(roomCode)
        when (res.code()) {
            200  -> res.body()?.toDomain()
            204  -> null  // sin pregunta activa
            else -> throw Exception("Error ${res.code()}")
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
        when (res.code()) {
            200  -> res.body()?.toDomain() ?: throw Exception("Sin respuesta")
            400  -> throw Exception("Ya respondiste esta pregunta o está cerrada")
            else -> throw Exception("Error ${res.code()}")
        }
    }
}
