package com.ale.quickscore.features.questions.domain.repositories

import com.ale.quickscore.features.questions.domain.entities.AnswerResult
import com.ale.quickscore.features.questions.domain.entities.Question

interface QuestionsRepository {
    suspend fun launchQuestion(roomCode: String, text: String, correctAnswer: String, points: Int): Result<Question>
    suspend fun getCurrentQuestion(roomCode: String): Result<Question?>
    suspend fun closeQuestion(roomCode: String, questionId: Int): Result<Unit>
    suspend fun submitAnswer(roomCode: String, questionId: Int, answer: String): Result<AnswerResult>
}
