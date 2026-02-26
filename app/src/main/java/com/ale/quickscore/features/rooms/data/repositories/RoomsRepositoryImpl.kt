package com.ale.quickscore.features.rooms.data.repositories

import com.ale.quickscore.features.rooms.data.datasources.remote.api.RoomsApi
import com.ale.quickscore.features.rooms.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.rooms.data.datasources.remote.model.AddPointsRequest
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.entities.Room
import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import javax.inject.Inject

class RoomsRepositoryImpl @Inject constructor(
    private val api: RoomsApi
) : RoomsRepository {

    override suspend fun createRoom(): Result<String> = runCatching {
        val res = api.createRoom()
        when (res.code()) {
            201, 200 -> res.body()?.get("code")?.toString() ?: throw Exception("Sin código de sala")
            403 -> throw Exception("No tienes permisos para crear salas (Debes ser Host)")
            401 -> throw Exception("Sesión no válida, por favor reingresa")
            else -> throw Exception("Error ${res.code()}: ${res.message()}")
        }
    }

    override suspend fun getRoom(code: String): Result<Room> = runCatching {
        val res = api.getRoom(code)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
        res.body()!!.toDomain()
    }

    override suspend fun joinRoom(code: String): Result<Unit> = runCatching {
        val res = api.joinRoom(code)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
    }

    override suspend fun startRoom(code: String): Result<Unit> = runCatching {
        val res = api.startRoom(code)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
    }

    override suspend fun endRoom(code: String): Result<Unit> = runCatching {
        val res = api.endRoom(code)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
    }

    override suspend fun getRanking(code: String): Result<List<RankingItem>> = runCatching {
        val res = api.getRanking(code)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
        res.body()?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun addScore(
        roomCode: String,
        targetUserId: Int,
        delta: Int
    ): Result<Unit> = runCatching {
        val res = api.addScore(roomCode, AddPointsRequest(delta, roomCode, targetUserId))
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
    }
}
