package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class RankingDto(
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("points") val points: Int?,
    @SerializedName("position") val position: Int?
)
