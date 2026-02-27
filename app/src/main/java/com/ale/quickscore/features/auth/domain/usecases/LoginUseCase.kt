package com.ale.quickscore.features.auth.domain.usecases


import com.ale.quickscore.features.auth.domain.entities.User
import com.ale.quickscore.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        repository.login(email, password)
}