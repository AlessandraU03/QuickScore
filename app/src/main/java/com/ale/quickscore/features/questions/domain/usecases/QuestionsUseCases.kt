package com.ale.quickscore.features.questions.domain.usecases

import com.ale.quickscore.features.questions.domain.repositories.QuestionsRepository
import javax.inject.Inject

class LaunchQuestionUseCase @Inject constructor(
    private val repository: QuestionsRepository
) {
    suspend operator fun invoke(
        roomCode: String,
        text: String,
        correctAnswer: String,
        points: Int
    ) = repository.launchQuestion(roomCode, text, correctAnswer, points)
}

class GetCurrentQuestionUseCase @Inject constructor(
    private val repository: QuestionsRepository
) {
    suspend operator fun invoke(roomCode: String) = repository.getCurrentQuestion(roomCode)
}

class CloseQuestionUseCase @Inject constructor(
    private val repository: QuestionsRepository
) {
    suspend operator fun invoke(roomCode: String, questionId: Int) =
        repository.closeQuestion(roomCode, questionId)
}

class SubmitAnswerUseCase @Inject constructor(
    private val repository: QuestionsRepository
) {
    suspend operator fun invoke(roomCode: String, questionId: Int, answer: String) =
        repository.submitAnswer(roomCode, questionId, answer)
}
