package com.ale.quickscore.features.auth.data.repositories

import com.ale.quickscore.core.data.local.dao.UserDao
import com.ale.quickscore.core.data.local.entities.toEntity
import com.ale.quickscore.core.di.TokenProvider
import com.ale.quickscore.core.di.SessionManager
import com.ale.quickscore.features.auth.data.datasources.remote.api.AuthApi
import com.ale.quickscore.features.auth.data.datasources.remote.mapper.toDomain
import com.ale.quickscore.features.auth.data.datasources.remote.models.LoginRequest
import com.ale.quickscore.features.auth.data.datasources.remote.models.RegisterRequest
import com.ale.quickscore.features.auth.domain.entities.User
import com.ale.quickscore.features.auth.domain.repositories.AuthRepository
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenProvider: TokenProvider,
    private val sessionManager: SessionManager,
    private val userDao: UserDao  // Agregamos el DAO para persistencia
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

                val user = body.toDomain()
                
                // Guardar usuario en la base de datos local
                userDao.clearUsers() // Limpiar usuarios anteriores
                userDao.insertUser(user.toEntity())

                Result.success(user)
            } else {
                val errorMsg = parseError(response.errorBody()?.string()) ?: "Error ${response.code()}"
                Result.failure(Exception(errorMsg))
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

                val user = body.toDomain()
                
                // Guardar usuario en la base de datos local
                userDao.clearUsers()
                userDao.insertUser(user.toEntity())

                Result.success(user)
            } else {
                val errorMsg = parseError(response.errorBody()?.string()) ?: "Error ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val json = JSONObject(errorBody)
            // Intenta buscar el mensaje en diferentes campos comunes
            json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optString("error").takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
