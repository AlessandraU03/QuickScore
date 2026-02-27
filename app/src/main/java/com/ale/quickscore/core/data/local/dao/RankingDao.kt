package com.ale.quickscore.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ale.quickscore.core.data.local.entities.RankingEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones relacionadas con el ranking de las salas
 */
@Dao
interface RankingDao {
    
    @Query("SELECT * FROM ranking WHERE roomCode = :roomCode ORDER BY score DESC")
    suspend fun getRankingByRoom(roomCode: String): List<RankingEntity>
    
    @Query("SELECT * FROM ranking WHERE roomCode = :roomCode ORDER BY score DESC")
    fun getRankingByRoomFlow(roomCode: String): Flow<List<RankingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRanking(ranking: RankingEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRankings(rankings: List<RankingEntity>)
    
    @Query("DELETE FROM ranking WHERE roomCode = :roomCode")
    suspend fun deleteRankingByRoom(roomCode: String)
    
    @Query("DELETE FROM ranking")
    suspend fun clearAllRankings()
}
