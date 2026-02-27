package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class ResetPointsRequest(
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("target_user_id") val targetUserId: Int
)
