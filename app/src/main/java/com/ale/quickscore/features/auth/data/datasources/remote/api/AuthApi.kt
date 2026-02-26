package com.ale.quickscore.features.auth.data.datasources.remote.api

import com.ale.quickscore.features.auth.data.datasources.remote.models.AuthResponse
import com.ale.quickscore.features.auth.data.datasources.remote.models.LoginRequest
import com.ale.quickscore.features.auth.data.datasources.remote.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>
}