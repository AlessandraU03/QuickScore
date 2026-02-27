package com.ale.quickscore.features.rooms.data.repositories

import com.ale.quickscore.core.data.local.dao.RankingDao
import com.ale.quickscore.core.data.local.dao.RoomDao
import com.ale.quickscore.core.data.local.entities.toEntity
import com.ale.quickscore.core.data.local.entities.toDomain
import com.ale.quickscore.features.rooms.data.datasources.remote.api.RoomsApi
import com.ale.quickscore.features.rooms.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.rooms.data.datasources.remote.model.AddPointsRequest
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.entities.Room
import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomsRepositoryImpl @Inject constructor(
    private val api: RoomsApi,
    private val roomDao: RoomDao,      // Agregamos DAOs para persistencia
    private val rankingDao: RankingDao
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
        // Estrategia: Intentar obtener de cache primero, luego actualizar desde servidor
        withContext(Dispatchers.IO) {
            // 1. Obtener de cache primero (para respuesta rápida)
            val cachedRoom = roomDao.getRoomByCode(code)?.toDomain()
            
            try {
                // 2. Intentar actualizar desde servidor
                val res = api.getRoom(code)
                if (res.isSuccessful) {
                    val room = res.body()!!.toDomain()
                    // 3. Guardar en cache
                    roomDao.insertRoom(room.toEntity())
                    room
                } else {
                    // Si falla el servidor pero tenemos cache, usarlo
                    cachedRoom ?: throw Exception("Error ${res.code()}")
                }
            } catch (e: Exception) {
                // Si hay error de red pero tenemos cache, usarlo
                cachedRoom ?: throw e
            }
        }
    }

    override suspend fun joinRoom(code: String): Result<Unit> = runCatching {
        val res = api.joinRoom(code)
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
    }

    override suspend fun startRoom(code: String): Result<Unit> = runCatching {
        val res = api.startRoom(code)
        if (res.isSuccessful) {
            // Actualizar el estado en cache
            withContext(Dispatchers.IO) {
                roomDao.updateRoomStatus(code, "active")
            }
        } else {
            throw Exception("Error ${res.code()}")
        }
    }

    override suspend fun endRoom(code: String): Result<Unit> = runCatching {
        val res = api.endRoom(code)
        if (res.isSuccessful) {
            // Actualizar el estado en cache
            withContext(Dispatchers.IO) {
                roomDao.updateRoomStatus(code, "ended")
            }
        } else {
            throw Exception("Error ${res.code()}")
        }
    }

    override suspend fun getRanking(code: String): Result<List<RankingItem>> = runCatching {
        withContext(Dispatchers.IO) {
            // 1. Obtener de cache primero
            val cachedRanking = rankingDao.getRankingByRoom(code).map { it.toDomain() }
            
            try {
                // 2. Intentar actualizar desde servidor
                val res = api.getRanking(code)
                if (res.isSuccessful) {
                    val ranking = res.body()?.map { it.toDomain() } ?: emptyList()
                    // 3. Guardar en cache
                    rankingDao.deleteRankingByRoom(code) // Limpiar ranking anterior
                    rankingDao.insertAllRankings(ranking.map { it.toEntity(code) })
                    ranking
                } else {
                    // Si falla el servidor pero tenemos cache, usarlo
                    cachedRanking.ifEmpty { throw Exception("Error ${res.code()}") }
                }
            } catch (e: Exception) {
                // Si hay error de red pero tenemos cache, usarlo
                cachedRanking.ifEmpty { throw e }
            }
        }
    }

    override suspend fun addScore(
        roomCode: String,
        targetUserId: Int,
        delta: Int
    ): Result<Unit> = runCatching {
        val res = api.addScore(roomCode, AddPointsRequest(delta, roomCode, targetUserId))
        if (!res.isSuccessful) throw Exception("Error ${res.code()}")
        // El ranking se actualizará cuando se llame a getRanking()
    }
}
