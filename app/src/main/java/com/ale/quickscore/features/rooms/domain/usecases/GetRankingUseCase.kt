package com.ale.quickscore.features.rooms.domain.usecases

import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class GetRankingUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    suspend operator fun invoke(code: String): Result<List<RankingItem>> =
        repository.getRanking(code)
}