package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class RoomResponse(
    @SerializedName("code") val code: String?,
    @SerializedName("host_id") val host_id: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("participants") val participants: List<ParticipantDto>?
)
