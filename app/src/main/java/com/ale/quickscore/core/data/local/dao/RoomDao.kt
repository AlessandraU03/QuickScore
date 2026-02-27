package com.ale.quickscore.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ale.quickscore.core.data.local.entities.RoomEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones relacionadas con las salas
 */
@Dao
interface RoomDao {
    
    @Query("SELECT * FROM rooms WHERE code = :code LIMIT 1")
    suspend fun getRoomByCode(code: String): RoomEntity?
    
    @Query("SELECT * FROM rooms WHERE code = :code LIMIT 1")
    fun getRoomByCodeFlow(code: String): Flow<RoomEntity?>
    
    @Query("SELECT * FROM rooms ORDER BY lastUpdatedTimestamp DESC")
    suspend fun getAllRooms(): List<RoomEntity>
    
    @Query("SELECT * FROM rooms ORDER BY lastUpdatedTimestamp DESC")
    fun getAllRoomsFlow(): Flow<List<RoomEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity)
    
    @Query("DELETE FROM rooms WHERE code = :code")
    suspend fun deleteRoom(code: String)
    
    @Query("DELETE FROM rooms")
    suspend fun clearAllRooms()
    
    @Query("UPDATE rooms SET status = :status, lastUpdatedTimestamp = :timestamp WHERE code = :code")
    suspend fun updateRoomStatus(code: String, status: String, timestamp: Long = System.currentTimeMillis())
}
