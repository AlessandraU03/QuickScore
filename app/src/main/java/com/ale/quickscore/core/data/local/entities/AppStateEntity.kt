package com.ale.quickscore.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para guardar el estado de la aplicación (última sala activa, etc.)
 * Permite restaurar la navegación cuando el usuario vuelve a abrir la app
 */
@Entity(tableName = "app_state")
data class AppStateEntity(
    @PrimaryKey
    val id: Int = 1, // Solo tendremos un registro
    val currentRoomCode: String? = null,
    val isInRoom: Boolean = false,
    val isHost: Boolean = false,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
