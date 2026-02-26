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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ale.quickscore.features.rooms.presentation.components.ConnectionStatusBadge
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
    val uiState by viewModel.uiState.collectAsState()

    // ✅ isHost se calcula comparando hostId con userId guardado en sesión
    val isHost = uiState.room?.hostId == viewModel.getCurrentUserId()

    LaunchedEffect(roomCode) {
        viewModel.initRoom(roomCode)
    }

    LaunchedEffect(uiState.sessionEnded) {
        if (uiState.sessionEnded) {
            onSessionEnded(roomCode)
        }
    }

    Scaffold { paddingValues ->
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

                    // Código de sala
                    item {
                        RoomCodeCard(code = roomCode)
                    }

                    // Botones host
                    if (isHost) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    }

                    // Participante esperando
                    if (!isHost && !uiState.sessionStarted) {
                        item {
                            WaitingMessage()
                        }
                    }

                    // Lista de participantes
                    uiState.room?.let { room ->
                        if (room.participants.isEmpty()) {
                            item {
                                Text(
                                    text = "Aún no hay participantes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "Participantes (${room.participants.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            items(
                                items = room.participants,
                                key = { it.userId }
                            ) { participant ->
                                ParticipantItem(
                                    participant = participant,
                                    isHost = isHost,
                                    sessionStarted = uiState.sessionStarted,
                                    onAddPoint = {
                                        viewModel.addScore(roomCode, participant.userId, 1)
                                    },
                                    onSubtractPoint = {
                                        viewModel.addScore(roomCode, participant.userId, -1)
                                    }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            // Error
            uiState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}
