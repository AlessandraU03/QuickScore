package com.ale.quickscore.features.auth.domain.usecases

import com.ale.quickscore.features.auth.domain.entities.User
import com.ale.quickscore.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        name: String,
        password: String,
        role: String
    ): Result<User> = repository.register(email, name, password, role)
}
