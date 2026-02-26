package com.ale.quickscore.features.auth.data.repositories

import com.ale.quickscore.core.di.TokenProvider
import com.ale.quickscore.core.di.SessionManager
import com.ale.quickscore.features.auth.data.datasources.remote.api.AuthApi
import com.ale.quickscore.features.auth.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.auth.data.datasources.remote.models.LoginRequest
import com.ale.quickscore.features.auth.data.datasources.remote.models.RegisterRequest
import com.ale.quickscore.features.auth.domain.entities.User
import com.ale.quickscore.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenProvider: TokenProvider,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))

                body.token?.let {
                    tokenProvider.saveToken(it)
                } ?: return Result.failure(Exception("No se recibió token de autenticación"))

                val userDto = body.user
                sessionManager.saveUser(
                    userId = userDto?.id ?: 0,
                    name = userDto?.name ?: "",
                    role = userDto?.role ?: ""
                )

                Result.success(body.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String, name: String, password: String, role: String
    ): Result<User> {
        return try {
            val response = api.register(RegisterRequest(email, name, password, role))
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))

                body.token?.let {
                    tokenProvider.saveToken(it)
                }

                val userDto = body.user
                sessionManager.saveUser(
                    userId = userDto?.id ?: 0,
                    name = userDto?.name ?: "",
                    role = userDto?.role ?: ""
                )

                Result.success(body.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
