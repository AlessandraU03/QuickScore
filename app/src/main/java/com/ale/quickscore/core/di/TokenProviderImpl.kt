package com.ale.quickscore.core.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProviderImpl @Inject constructor(
    private val sessionManager: SessionManager
) : TokenProvider {
    
    override fun getToken(): String? = sessionManager.getToken()
    
    override fun saveToken(token: String) {
        sessionManager.saveToken(token) // ✅ Ahora sí se guarda
    }
    
    override fun clearToken() = sessionManager.clear()
}
