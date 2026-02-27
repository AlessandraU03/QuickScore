package com.ale.quickscore.features.rooms.domain.repositories

import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.entities.Room

interface RoomsRepository {
    suspend fun createRoom(): Result<String>
    suspend fun getRoom(code: String): Result<Room>
    suspend fun getCurrentRoom(): Result<Room>
    suspend fun joinRoom(code: String): Result<Unit>
    suspend fun startRoom(code: String): Result<Unit>
    suspend fun endRoom(code: String): Result<Unit>
    suspend fun getRanking(code: String): Result<List<RankingItem>>
    suspend fun addScore(roomCode: String, targetUserId: Int, delta: Int): Result<Unit>
}
