package com.ale.quickscore.features.questions.data.datasources.remote.mapper

import com.ale.quickscore.features.questions.data.datasources.remote.model.AnswerResultDto
import com.ale.quickscore.features.questions.data.datasources.remote.model.QuestionDto
import com.ale.quickscore.features.questions.domain.entities.AnswerResult
import com.ale.quickscore.features.questions.domain.entities.Question

fun QuestionDto.toDomain() = Question(
    id      = id ?: 0,
    roomId  = room_id ?: 0,
    text    = text ?: "",
    points  = points ?: 0,
    status  = status ?: ""
)

fun AnswerResultDto.toDomain() = AnswerResult(
    isCorrect    = is_correct ?: false,
    pointsEarned = points_earned ?: 0,
    message      = message ?: ""
)
