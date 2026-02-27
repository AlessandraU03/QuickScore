package com.ale.quickscore.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ale.quickscore.features.auth.domain.entities.User

/**
 * Entidad de Room para cachear datos del usuario autenticado
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val token: String,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)

/**
 * Mappers entre UserEntity y User (domain)
 */
fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role,
    token = token
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    role = role,
    token = token
)
