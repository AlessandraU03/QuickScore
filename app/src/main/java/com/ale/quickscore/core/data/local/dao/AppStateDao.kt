package com.ale.quickscore.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ale.quickscore.core.data.local.entities.AppStateEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar el estado de la aplicación
 */
@Dao
interface AppStateDao {
    
    @Query("SELECT * FROM app_state WHERE id = 1 LIMIT 1")
    suspend fun getAppState(): AppStateEntity?
    
    @Query("SELECT * FROM app_state WHERE id = 1 LIMIT 1")
    fun getAppStateFlow(): Flow<AppStateEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppState(state: AppStateEntity)
    
    @Query("DELETE FROM app_state")
    suspend fun clearAppState()
    
    @Query("UPDATE app_state SET currentRoomCode = :roomCode, isInRoom = :isInRoom, lastUpdatedTimestamp = :timestamp WHERE id = 1")
    suspend fun updateCurrentRoom(
        roomCode: String?,
        isInRoom: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
}
