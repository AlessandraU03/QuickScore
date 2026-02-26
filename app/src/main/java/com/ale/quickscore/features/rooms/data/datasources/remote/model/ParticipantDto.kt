package com.ale.quickscore.features.rooms.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class ParticipantDto(
    @SerializedName("user_id") val user_id: Int?,
    @SerializedName("user_name") val user_name: String?, // Para /participants
    @SerializedName("name") val name: String?,           // Para otros endpoints
    @SerializedName("score") val score: Int? = 0
) {
    // Propiedad auxiliar para obtener el nombre sin importar cuál campo venga
    val displayName: String get() = user_name ?: name ?: "Sin nombre"
}
