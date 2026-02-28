package com.ale.quickscore.features.scores.data.di

import com.ale.quickscore.features.scores.data.repositories.ScoresRepositoryImpl
import com.ale.quickscore.features.scores.domain.repositories.ScoresRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScoresRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScoresRepository(
        scoresRepositoryImpl: ScoresRepositoryImpl
    ): ScoresRepository
}
