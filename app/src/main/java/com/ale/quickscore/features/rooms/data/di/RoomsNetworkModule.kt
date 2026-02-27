package com.ale.quickscore.features.rooms.data.di

import com.ale.quickscore.core.di.AuthRetrofit
import com.ale.quickscore.features.rooms.data.datasources.remote.api.RoomsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomsNetworkModule {

    @Provides
    @Singleton
    fun provideRoomsApi(@AuthRetrofit retrofit: Retrofit): RoomsApi =
        retrofit.create(RoomsApi::class.java)
}