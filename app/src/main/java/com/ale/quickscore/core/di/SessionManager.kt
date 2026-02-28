package com.ale.quickscore.core.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("quickscore_session", Context.MODE_PRIVATE)

    fun saveUser(userId: Int, name: String, role: String) {
        prefs.edit()
            .putInt("user_id", userId)
            .putString("user_name", name)
            .putString("user_role", role)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun getUserRole(): String = prefs.getString("user_role", "") ?: ""
    fun isHost(): Boolean = getUserRole() == "host"
    fun getToken(): String? = prefs.getString("auth_token", null)

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    // Funciones para persistir la sala actual
    fun saveCurrentRoom(code: String) {
        prefs.edit().putString("current_room_code", code).apply()
    }

    fun getCurrentRoomCode(): String? {
        return prefs.getString("current_room_code", null)
    }

    fun clearRoom() {
        prefs.edit().remove("current_room_code").apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}