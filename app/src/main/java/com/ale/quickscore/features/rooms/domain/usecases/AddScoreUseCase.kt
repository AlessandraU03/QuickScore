package com.ale.quickscore.features.rooms.domain.usecases

import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class AddScoreUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    suspend operator fun invoke(
        roomCode: String,
        targetUserId: Int,
        delta: Int
    ): Result<Unit> = repository.addScore(roomCode, targetUserId, delta)
}