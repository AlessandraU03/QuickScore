package com.ale.quickscore.core.data.local.di

import android.content.Context
import androidx.room.Room
import com.ale.quickscore.core.data.local.QuickScoreDatabase
import com.ale.quickscore.core.data.local.dao.AppStateDao
import com.ale.quickscore.core.data.local.dao.RankingDao
import com.ale.quickscore.core.data.local.dao.RoomDao
import com.ale.quickscore.core.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer la base de datos y los DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideQuickScoreDatabase(
        @ApplicationContext context: Context
    ): QuickScoreDatabase {
        return Room.databaseBuilder(
            context,
            QuickScoreDatabase::class.java,
            QuickScoreDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // En producción, usa migraciones apropiadas
            .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: QuickScoreDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideRoomDao(database: QuickScoreDatabase): RoomDao {
        return database.roomDao()
    }
    
    @Provides
    @Singleton
    fun provideRankingDao(database: QuickScoreDatabase): RankingDao {
        return database.rankingDao()
    }
    
    @Provides
    @Singleton
    fun provideAppStateDao(database: QuickScoreDatabase): AppStateDao {
        return database.appStateDao()
    }
}
