package com.ale.quickscore.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.quickscore.features.auth.domain.usecases.GetCurrentUserUseCase
import com.ale.quickscore.features.auth.domain.usecases.LoginUseCase
import com.ale.quickscore.features.auth.domain.usecases.LogoutUseCase
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
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState = _uiState.asStateFlow()

    init {
        // Verificar si hay una sesión activa al iniciar
        checkCurrentSession()
    }

    /**
     * Verifica si hay una sesión activa guardada
     * Mejora de persistencia: permite auto-login
     */
    private fun checkCurrentSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCurrentUserUseCase().fold(
                onSuccess = { user ->
                    if (user != null) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                user = user, 
                                isSuccess = true
                            ) 
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                }
            )
        }
    }

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

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onAcceptTermsChange(accept: Boolean) {
        _uiState.update { it.copy(acceptTerms = accept) }
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
        if (!state.acceptTerms) {
            _uiState.update { it.copy(error = "Debes aceptar los términos y condiciones") }
            return
        }

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

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            resetState()
        }
    }

    fun resetState() {
        _uiState.update { AuthUIState() }
    }
}
