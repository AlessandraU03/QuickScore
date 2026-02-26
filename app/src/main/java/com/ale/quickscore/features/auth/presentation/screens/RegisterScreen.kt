package com.ale.quickscore.features.auth.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.auth.presentation.components.AuthButton
import com.ale.quickscore.features.auth.presentation.components.AuthErrorText
import com.ale.quickscore.features.auth.presentation.components.AuthTextField
import com.ale.quickscore.features.auth.presentation.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "¡Registro exitoso! Por favor inicia sesión", Toast.LENGTH_LONG).show()
            viewModel.resetState()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text(
                text = "QuickScore",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Únete a QuickScore y empieza a competir en torneos reales",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF79747E),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Role Selector (Customized)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFF3EDFF)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                val roles = listOf("participant" to "Participante", "host" to "Host")
                roles.forEach { (roleKey, roleLabel) ->
                    val isSelected = uiState.role == roleKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) Color(0xFF7C4DFF) else Color.Transparent)
                            .clickable { viewModel.onRoleChange(roleKey) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = roleLabel,
                            color = if (isSelected) Color.White else Color(0xFF79747E),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = "Nombre completo",
            placeholder = "Ej. Juan Pérez",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF948F99)) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Email",
            placeholder = "tu@email.com",
            leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF948F99)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Contraseña",
            placeholder = "Mínimo 8 caracteres",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF948F99)) },
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF948F99)
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.acceptTerms,
                onCheckedChange = { viewModel.onAcceptTermsChange(it) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF7C4DFF))
            )
            Text(
                text = buildAnnotatedString {
                    append("Acepto los ")
                    withStyle(style = SpanStyle(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)) {
                        append("Términos de Servicio")
                    }
                    append(" y la ")
                    withStyle(style = SpanStyle(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)) {
                        append("Política de Privacidad")
                    }
                    append(".")
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        AuthErrorText(error = uiState.error)
        Spacer(modifier = Modifier.height(24.dp))

        AuthButton(
            text = "Crear cuenta",
            onClick = { viewModel.register() },
            isLoading = uiState.isLoading,
            icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = buildAnnotatedString {
                append("¿Ya tienes cuenta? ")
                withStyle(style = SpanStyle(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold)) {
                    append("Inicia sesión")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
