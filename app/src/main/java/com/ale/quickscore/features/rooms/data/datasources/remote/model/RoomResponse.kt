package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class RoomResponse(
    @SerializedName("code") val code: String?,
    @SerializedName("host_id") val hostId: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("participants") val participants: List<ParticipantDto>?
)
