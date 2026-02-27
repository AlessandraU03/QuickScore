package com.ale.quickscore.features.rooms.domain.usecases

import com.ale.quickscore.features.rooms.domain.entities.Room
import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class GetCurrentRoomUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    suspend operator fun invoke(): Result<Room> = repository.getCurrentRoom()
}
