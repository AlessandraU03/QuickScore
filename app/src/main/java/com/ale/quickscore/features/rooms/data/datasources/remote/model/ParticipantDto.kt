package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class ParticipantDto(
    @SerializedName("user_id") val user_id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("score") val score: Int?
)
