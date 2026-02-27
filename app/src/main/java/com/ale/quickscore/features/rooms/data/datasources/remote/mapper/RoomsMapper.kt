package com.ale.quickscore.features.rooms.data.datasources.remote.mapper

import com.ale.quickscore.features.rooms.data.datasources.remote.model.ParticipantDto
import com.ale.quickscore.features.rooms.data.datasources.remote.model.RankingDto
import com.ale.quickscore.features.rooms.data.datasources.remote.model.RoomResponse
import com.ale.quickscore.features.rooms.domain.entities.Participant
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.entities.Room

fun RoomResponse.toDomain(): Room = Room(
    code = code ?: "",
    hostId = hostId ?: 0,
    status = status ?: "",
    participants = participants?.map { it.toDomain() } ?: emptyList()
)

fun ParticipantDto.toDomain(): Participant = Participant(
    userId = userId ?: 0,
    name = displayName,
    score = points ?: 0
)

fun RankingDto.toDomain(): RankingItem = RankingItem(
    userId = userId ?: 0,
    name = userName ?: "",
    score = points ?: 0
)
