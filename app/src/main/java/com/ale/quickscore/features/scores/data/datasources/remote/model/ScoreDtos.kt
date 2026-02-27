package com.ale.quickscore.features.scores.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class AddPointsRequest(
    @SerializedName("delta") val delta: Int,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("target_user_id") val targetUserId: Int
)

data class ResetPointsRequest(
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("target_user_id") val targetUserId: Int
)
