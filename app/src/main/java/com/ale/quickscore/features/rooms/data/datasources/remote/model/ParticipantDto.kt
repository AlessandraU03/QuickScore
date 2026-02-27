package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class ParticipantDto(
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("points") val points: Int? = 0
) {
    val displayName: String get() = userName ?: name ?: "Sin nombre"
}
