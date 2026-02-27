package com.ale.quickscore.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ale.quickscore.core.data.local.dao.AppStateDao
import com.ale.quickscore.core.data.local.dao.RankingDao
import com.ale.quickscore.core.data.local.dao.RoomDao
import com.ale.quickscore.core.data.local.dao.UserDao
import com.ale.quickscore.core.data.local.entities.AppStateEntity
import com.ale.quickscore.core.data.local.entities.RankingEntity
import com.ale.quickscore.core.data.local.entities.RoomConverters
import com.ale.quickscore.core.data.local.entities.RoomEntity
import com.ale.quickscore.core.data.local.entities.UserEntity

/**
 * Base de datos principal de la aplicación usando Room
 * Versión 1: Primera implementación con todas las entidades básicas
 */
@Database(
    entities = [
        UserEntity::class,
        RoomEntity::class,
        RankingEntity::class,
        AppStateEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class QuickScoreDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun roomDao(): RoomDao
    abstract fun rankingDao(): RankingDao
    abstract fun appStateDao(): AppStateDao
    
    companion object {
        const val DATABASE_NAME = "quickscore_database"
    }
}
