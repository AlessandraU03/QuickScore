package com.ale.quickscore.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.features.auth.domain.usecases.LoginUseCase
import com.ale.quickscore.features.auth.domain.usecases.RegisterUseCase
import com.ale.quickscore.features.auth.presentation.screens.AuthUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onRoleChange(role: String) {
        _uiState.update { it.copy(role = role) }
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { user ->
                        currentState.copy(isLoading = false, user = user, isSuccess = true)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun register() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = registerUseCase(state.email, state.name, state.password, state.role)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { user ->
                        currentState.copy(isLoading = false, user = user, isSuccess = true)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUIState() }
    }
}
