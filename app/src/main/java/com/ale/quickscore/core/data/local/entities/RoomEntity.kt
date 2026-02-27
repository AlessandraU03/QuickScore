package com.ale.quickscore.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.ale.quickscore.features.rooms.domain.entities.Participant
import com.ale.quickscore.features.rooms.domain.entities.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Entidad de Room para cachear información de las salas
 */
@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey
    val code: String,
    val hostId: Int,
    val status: String,
    val participantsJson: String, // JSON serializado de la lista de participantes
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Type Converters para Room
 */
class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromParticipantsList(participants: List<Participant>): String {
        return gson.toJson(participants)
    }

    @TypeConverter
    fun toParticipantsList(json: String): List<Participant> {
        val type = object : TypeToken<List<Participant>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}

/**
 * Mappers entre RoomEntity y Room (domain)
 */
fun RoomEntity.toDomain(): Room {
    val participants = try {
        val gson = Gson()
        val type = object : TypeToken<List<Participant>>() {}.type
        gson.fromJson<List<Participant>>(participantsJson, type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    return Room(
        code = code,
        hostId = hostId,
        status = status,
        participants = participants
    )
}

fun Room.toEntity() = RoomEntity(
    code = code,
    hostId = hostId,
    status = status,
    participantsJson = Gson().toJson(participants)
)
