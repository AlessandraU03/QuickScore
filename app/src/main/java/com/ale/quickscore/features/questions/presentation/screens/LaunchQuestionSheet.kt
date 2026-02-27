package com.ale.quickscore.features.questions.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.auth.presentation.components.AuthButton
import com.ale.quickscore.features.auth.presentation.components.AuthErrorText
import com.ale.quickscore.features.questions.presentation.viewmodels.QuestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchQuestionSheet(
    roomCode: String,
    onDismiss: () -> Unit,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            viewModel.resetForm()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            )
        },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lanzar Pregunta",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF3F4F9), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF948F99)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Escribe tu pregunta
            QuestionLabel(icon = Icons.Default.HelpOutline, text = "Escribe tu pregunta", color = Color(0xFF7C4DFF))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.text,
                onValueChange = viewModel::onTextChange,
                placeholder = { Text("Ej: ¿Cuándo nació Benito Juárez?", color = Color(0xFF948F99)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8EAF6),
                    focusedBorderColor = Color(0xFF7C4DFF),
                    unfocusedContainerColor = Color(0xFFF8F9FE),
                    focusedContainerColor = Color(0xFFF8F9FE)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Respuesta correcta
            QuestionLabel(icon = Icons.Default.CheckCircle, text = "Respuesta correcta", color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.correctAnswer,
                onValueChange = viewModel::onCorrectAnswerChange,
                placeholder = { Text("Ej: 21 de marzo", color = Color(0xFF948F99)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8EAF6),
                    focusedBorderColor = Color(0xFF7C4DFF),
                    unfocusedContainerColor = Color(0xFFF8F9FE),
                    focusedContainerColor = Color(0xFFF8F9FE)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Valor de la pregunta
            QuestionLabel(icon = Icons.Default.Stars, text = "Valor de la pregunta", color = Color(0xFFFF9800))
            Spacer(modifier = Modifier.height(12.dp))

            // Point Selectors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFF3F4F9), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val pointOptions = listOf("10", "25", "50", "100")
                pointOptions.forEach { points ->
                    val isSelected = uiState.points == points
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { viewModel.onPointsChange(points) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$points pts",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF79747E)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar error si existe
            AuthErrorText(error = uiState.error)

            Spacer(modifier = Modifier.height(8.dp))

            // Launch Button
            AuthButton(
                text = "Lanzar pregunta ahora",
                onClick = { viewModel.launchQuestion(roomCode) },
                isLoading = uiState.isLoading,
                icon = {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Guardar como borrador",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF79747E),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { /* TODO */ }
            )
        }
    }
}

@Composable
fun QuestionLabel(icon: ImageVector, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF49454F)
            )
        )
    }
}
