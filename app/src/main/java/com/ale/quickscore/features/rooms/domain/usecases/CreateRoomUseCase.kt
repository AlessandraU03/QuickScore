package com.ale.quickscore.features.rooms.domain.usecases

import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    suspend operator fun invoke(): Result<String> = repository.createRoom()
}