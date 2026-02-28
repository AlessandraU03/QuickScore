package com.ale.quickscore.features.scores.data.repositories

import com.ale.quickscore.features.rooms.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.scores.data.datasources.remote.api.ScoresApi
import com.ale.quickscore.features.scores.data.datasources.remote.model.AddPointsRequest
import com.ale.quickscore.features.scores.data.datasources.remote.model.ResetPointsRequest
import com.ale.quickscore.features.scores.domain.repositories.ScoresRepository
import javax.inject.Inject

class ScoresRepositoryImpl @Inject constructor(
    private val api: ScoresApi
) : ScoresRepository {

    override suspend fun addScore(roomCode: String, targetUserId: Int, delta: Int): Result<Unit> = runCatching {
        val res = api.addScore(roomCode, AddPointsRequest(delta, roomCode, targetUserId))
        if (!res.isSuccessful) throw Exception("Error al agregar puntos")
    }

    override suspend fun getRanking(roomCode: String): Result<List<RankingItem>> = runCatching {
        val res = api.getRanking(roomCode)
        if (!res.isSuccessful) throw Exception("Error al obtener ranking")
        res.body()?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun resetUserPoints(roomCode: String, targetUserId: Int): Result<Unit> = runCatching {
        val res = api.resetUserPoints(roomCode, ResetPointsRequest(roomCode, targetUserId))
        if (!res.isSuccessful) throw Exception("Error al resetear puntos del usuario")
    }

    override suspend fun resetAllPoints(roomCode: String): Result<Unit> = runCatching {
        val res = api.resetAllPoints(roomCode)
        if (!res.isSuccessful) throw Exception("Error al resetear todos los puntos")
    }
}
