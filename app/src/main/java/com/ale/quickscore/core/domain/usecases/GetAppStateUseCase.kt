package com.ale.quickscore.core.domain.usecases

import com.ale.quickscore.core.data.local.dao.AppStateDao
import com.ale.quickscore.core.data.local.entities.AppStateEntity
import javax.inject.Inject

/**
 * UseCase para obtener el estado guardado de la aplicación
 * Permite restaurar la navegación cuando la app se reinicia
 */
class GetAppStateUseCase @Inject constructor(
    private val appStateDao: AppStateDao
) {
    suspend operator fun invoke(): Result<AppStateEntity?> = runCatching {
        appStateDao.getAppState()
    }
}
