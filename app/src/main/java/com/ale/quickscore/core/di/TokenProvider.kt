package com.ale.quickscore.core.di

interface TokenProvider {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}
