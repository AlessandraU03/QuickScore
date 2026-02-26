package com.ale.quickscore.features.rooms.data.datasources.remote.mapper

import com.ale.quickscore.features.rooms.data.datasources.remote.model.ParticipantDto
import com.ale.quickscore.features.rooms.data.datasources.remote.model.RankingDto
import com.ale.quickscore.features.rooms.data.datasources.remote.model.RoomResponse
import com.ale.quickscore.features.rooms.domain.entities.Participant
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.domain.entities.Room

fun RoomResponse.toDomain(): Room = Room(
    code = code ?: "",
    hostId = host_id ?: 0,
    status = status ?: "",
    participants = participants?.map { it.toDomain() } ?: emptyList()
)

fun ParticipantDto.toDomain(): Participant = Participant(
    userId = user_id ?: 0,
    name = name ?: "",
    score = score ?: 0
)

fun RankingDto.toDomain(): RankingItem = RankingItem(
    userId = user_id ?: 0,
    name = name ?: "",
    score = score ?: 0
)