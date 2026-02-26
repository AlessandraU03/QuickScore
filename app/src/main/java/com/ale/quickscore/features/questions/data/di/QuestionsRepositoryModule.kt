package com.ale.quickscore.features.questions.data.di

import com.ale.quickscore.features.questions.data.repositories.QuestionsRepositoryImpl
import com.ale.quickscore.features.questions.domain.repositories.QuestionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
abstract class QuestionsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindQuestionsRepository(impl: QuestionsRepositoryImpl): QuestionsRepository
}
