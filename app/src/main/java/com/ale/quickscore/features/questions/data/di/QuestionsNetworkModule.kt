package com.ale.quickscore.features.questions.data.di

import com.ale.quickscore.core.di.AuthRetrofit
import com.ale.quickscore.features.questions.data.datasources.remote.api.QuestionsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuestionsNetworkModule {
    @Provides
    @Singleton
    fun provideQuestionsApi(@AuthRetrofit retrofit: Retrofit): QuestionsApi =
        retrofit.create(QuestionsApi::class.java)
}
