package com.ale.quickscore.features.scores.data.di

import com.ale.quickscore.core.di.AuthRetrofit
import com.ale.quickscore.features.scores.data.datasources.remote.api.ScoresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScoresNetworkModule {

    @Provides
    @Singleton
    fun provideScoresApi(@AuthRetrofit retrofit: Retrofit): ScoresApi =
        retrofit.create(ScoresApi::class.java)
}
