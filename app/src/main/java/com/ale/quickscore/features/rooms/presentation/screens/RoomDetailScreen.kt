package com.ale.quickscore.features.rooms.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.questions.presentation.components.ActiveQuestionCard
import com.ale.quickscore.features.questions.presentation.components.AnswerResultBanner
import com.ale.quickscore.features.questions.presentation.components.HostQuestionCard
import com.ale.quickscore.features.questions.presentation.screens.LaunchQuestionSheet
import com.ale.quickscore.features.rooms.presentation.components.ConnectionStatusBadge
import com.ale.quickscore.features.rooms.presentation.components.OnlineUsersRow
import com.ale.quickscore.features.rooms.presentation.components.ParticipantItem
import com.ale.quickscore.features.rooms.presentation.components.RoomCodeCard
import com.ale.quickscore.features.rooms.presentation.components.SessionButton
import com.ale.quickscore.features.rooms.presentation.components.WaitingMessage
import com.ale.quickscore.features.rooms.presentation.viewmodels.RoomViewModel

@Composable
fun RoomDetailScreen(
    roomCode: String,
    onSessionEnded: (String) -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isHost = uiState.room?.hostId == viewModel.getCurrentUserId()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLaunchSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(roomCode) { viewModel.initRoom(roomCode) }

    LaunchedEffect(uiState.sessionEnded) {
        if (uiState.sessionEnded) onSessionEnded(roomCode)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Diálogo de confirmación para kick
    if (uiState.showKickDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onKickDismiss() },
            title = { Text("Expulsar participante") },
            text = { Text("¿Seguro que quieres expulsar a ${uiState.kickTargetName}?") },
            confirmButton = {
                TextButton(onClick = {
                    uiState.kickTargetId?.let { viewModel.addScore(roomCode, it, 0) }
                    viewModel.onKickDismiss()
                }) { Text("Expulsar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onKickDismiss() }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isHost && uiState.sessionStarted && uiState.activeQuestion == null) {
                ExtendedFloatingActionButton(
                    onClick = { showLaunchSheet = true },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    text = { Text("Lanzar pregunta") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.room == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHost) "Vista Host" else "Vista Participante",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            ConnectionStatusBadge(isConnected = uiState.isConnected)
                        }
                    }

                    item { RoomCodeCard(code = roomCode) }

                    // Usuarios en línea
                    if (uiState.onlineUsers.isNotEmpty()) {
                        item { OnlineUsersRow(onlineUsers = uiState.onlineUsers) }
                    }

                    // Botones de sesión — solo host
                    if (isHost) {
                        item {
                            if (!uiState.sessionStarted) {
                                SessionButton(
                                    text = "Iniciar sesión",
                                    onClick = { viewModel.startRoom(roomCode) }
                                )
                            } else {
                                SessionButton(
                                    text = "Finalizar sesión",
                                    onClick = { viewModel.endRoom(roomCode) },
                                    isDestructive = true
                                )
                            }
                        }
                    }

                    // Pregunta activa — vista HOST
                    if (isHost) {
                        uiState.activeQuestion?.let { q ->
                            item {
                                HostQuestionCard(
                                    question = q,
                                    onClose  = { viewModel.closeQuestion() }
                                )
                            }
                        }
                    }

                    // Pregunta activa — vista PARTICIPANTE
                    if (!isHost && uiState.sessionStarted) {
                        uiState.activeQuestion?.let { q ->
                            item {
                                ActiveQuestionCard(
                                    question  = q,
                                    isLoading = uiState.isAnswering,
                                    onSubmit  = { answer -> viewModel.submitAnswer(answer) }
                                )
                            }
                        }
                    }

                    // Banner resultado respuesta
                    uiState.lastAnswerCorrect?.let { correct ->
                        item {
                            AnswerResultBanner(
                                isCorrect    = correct,
                                pointsEarned = uiState.lastAnswerPoints,
                                message      = uiState.lastAnswerMessage
                            )
                        }
                    }

                    // Esperando sesión — participante
                    if (!isHost && !uiState.sessionStarted) {
                        item { WaitingMessage() }
                    }

                    // Lista participantes / ranking
                    uiState.room?.let { room ->
                        if (room.participants.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Participantes (${room.participants.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            items(
                                items = room.participants,
                                key   = { it.userId }
                            ) { participant ->
                                ParticipantItem(
                                    participant     = participant,
                                    isHost          = isHost,
                                    sessionStarted  = uiState.sessionStarted,
                                    onAddPoint      = { viewModel.addScore(roomCode, participant.userId, 1) },
                                    onSubtractPoint = { viewModel.addScore(roomCode, participant.userId, -1) },
                                    onKick          = { viewModel.onKickRequest(participant.userId, participant.name) }
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "Aún no hay participantes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showLaunchSheet) {
        LaunchQuestionSheet(
            roomCode  = roomCode,
            onDismiss = { showLaunchSheet = false }
        )
    }
}
