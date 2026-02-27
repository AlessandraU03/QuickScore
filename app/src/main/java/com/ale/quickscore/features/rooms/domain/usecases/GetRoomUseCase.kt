package com.ale.quickscore.features.rooms.domain.usecases

import com.ale.quickscore.features.rooms.domain.entities.Room
import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class GetRoomUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    suspend operator fun invoke(code: String): Result<Room> = repository.getRoom(code)
}