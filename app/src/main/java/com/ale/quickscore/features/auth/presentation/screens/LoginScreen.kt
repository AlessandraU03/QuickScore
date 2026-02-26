package com.ale.quickscore.features.auth.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.auth.presentation.components.AuthButton
import com.ale.quickscore.features.auth.presentation.components.AuthErrorText
import com.ale.quickscore.features.auth.presentation.components.AuthTextField
import com.ale.quickscore.features.auth.presentation.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (isHost: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            val isHost = uiState.user?.role == "host"
            viewModel.resetState()
            onLoginSuccess(isHost)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "QuickScore",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        AuthTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Contraseña",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        AuthErrorText(error = uiState.error)

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = "Iniciar sesión",
            onClick = { viewModel.login() },
            isLoading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
