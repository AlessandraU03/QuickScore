package com.ale.quickscore.features.rooms.data.di

import com.ale.quickscore.features.rooms.data.repositories.RoomsRepositoryImpl
import com.ale.quickscore.features.rooms.domain.repositories.RoomsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRoomsRepository(
        impl: RoomsRepositoryImpl
    ): RoomsRepository
}