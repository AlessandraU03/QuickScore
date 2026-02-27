package com.ale.quickscore.features.auth.domain.usecases

import com.ale.quickscore.core.data.local.dao.UserDao
import com.ale.quickscore.core.data.local.dao.AppStateDao
import com.ale.quickscore.core.data.local.dao.RoomDao
import com.ale.quickscore.core.data.local.dao.RankingDao
import com.ale.quickscore.core.di.SessionManager
import com.ale.quickscore.core.di.TokenProvider
import javax.inject.Inject

/**
 * UseCase para cerrar sesión del usuario
 * Limpia todos los datos persistentes y el estado de la aplicación
 */
class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val tokenProvider: TokenProvider,
    private val userDao: UserDao,
    private val roomDao: RoomDao,
    private val rankingDao: RankingDao,
    private val appStateDao: AppStateDao
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        // Limpiar SharedPreferences
        sessionManager.clear()
        tokenProvider.clearToken()
        
        // Limpiar base de datos local
        userDao.clearUsers()
        roomDao.clearAllRooms()
        rankingDao.clearAllRankings()
        appStateDao.clearAppState()
    }
}
