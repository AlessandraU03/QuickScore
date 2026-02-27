package com.ale.quickscore.features.scores.domain.repositories

import com.ale.quickscore.features.rooms.domain.entities.RankingItem

interface ScoresRepository {
    suspend fun addScore(roomCode: String, targetUserId: Int, delta: Int): Result<Unit>
    suspend fun getRanking(roomCode: String): Result<List<RankingItem>>
    suspend fun resetUserPoints(roomCode: String, targetUserId: Int): Result<Unit>
    suspend fun resetAllPoints(roomCode: String): Result<Unit>
}
