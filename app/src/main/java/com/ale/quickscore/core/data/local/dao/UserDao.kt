package com.ale.quickscore.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ale.quickscore.core.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones relacionadas con el usuario autenticado
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun clearUsers()
    
    @Query("UPDATE users SET token = :token WHERE id = :userId")
    suspend fun updateToken(userId: Int, token: String)
}
