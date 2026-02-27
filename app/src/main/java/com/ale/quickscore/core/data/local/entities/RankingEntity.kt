package com.ale.quickscore.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ale.quickscore.features.rooms.domain.entities.RankingItem

/**
 * Entidad de Room para cachear el ranking de una sala
 */
@Entity(
    tableName = "ranking",
    primaryKeys = ["roomCode", "userId"]
)
data class RankingEntity(
    val roomCode: String,
    val userId: Int,
    val name: String,
    val score: Int,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Mappers entre RankingEntity y RankingItem (domain)
 */
fun RankingEntity.toDomain() = RankingItem(
    userId = userId,
    name = name,
    score = score
)

fun RankingItem.toEntity(roomCode: String) = RankingEntity(
    roomCode = roomCode,
    userId = userId,
    name = name,
    score = score
)
