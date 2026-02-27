package com.ale.quickscore.features.auth.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

// ---- Request ----
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String
)

// ---- Response ----
data class AuthResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: UserDto? // Los datos están dentro de "user"
)

data class UserDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role") val role: String?
)
