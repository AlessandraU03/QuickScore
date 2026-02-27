package com.ale.quickscore.features.auth.domain.usecases

import com.ale.quickscore.core.data.local.dao.UserDao
import com.ale.quickscore.core.data.local.entities.toDomain
import com.ale.quickscore.features.auth.domain.entities.User
import javax.inject.Inject

/**
 * UseCase para verificar si hay una sesión activa
 * Parte de la mejora de persistencia - permite auto-login
 */
class GetCurrentUserUseCase @Inject constructor(
    private val userDao: UserDao
) {
    suspend operator fun invoke(): Result<User?> = runCatching {
        userDao.getCurrentUser()?.toDomain()
    }
}
