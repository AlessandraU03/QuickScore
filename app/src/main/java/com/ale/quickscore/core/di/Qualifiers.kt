package com.ale.quickscore.core.di

import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoomsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ScoresRetrofit