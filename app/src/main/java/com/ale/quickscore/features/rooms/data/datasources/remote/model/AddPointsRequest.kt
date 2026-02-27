package com.ale.quickscore.features.rooms.data.datasources.remote.model

data class AddPointsRequest(
    val delta: Int,
    val room_code: String,
    val target_user_id: Int
)