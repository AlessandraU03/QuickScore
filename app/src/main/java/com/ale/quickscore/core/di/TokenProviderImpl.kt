package com.ale.quickscore.core.di

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    override fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    override fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
    }
}
