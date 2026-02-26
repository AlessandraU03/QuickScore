package com.ale.quickscore.features.rooms.domain.usecases

import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class StartRoomUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    suspend operator fun invoke(code: String): Result<Unit> = repository.startRoom(code)
}