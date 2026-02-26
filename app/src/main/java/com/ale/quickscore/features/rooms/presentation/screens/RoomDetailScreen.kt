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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.ale.quickscore.features.rooms.presentation.components.ConnectionStatusBadge
import com.ale.quickscore.features.rooms.presentation.components.OnlineUsersRow
import com.ale.quickscore.features.rooms.presentation.components.ParticipantItem
import com.ale.quickscore.features.rooms.presentation.components.RoomCodeCard
import com.ale.quickscore.features.rooms.presentation.components.SessionButton
import com.ale.quickscore.features.rooms.presentation.components.WaitingMessage
import com.ale.quickscore.features.rooms.presentation.viewmodels.RoomViewModel
import kotlinx.coroutines.delay

@Composable
fun RoomDetailScreen(
    roomCode: String,
    onSessionEnded: (String) -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isHost = uiState.room?.hostId == viewModel.getCurrentUserId()
    var showLaunchSheet by remember { mutableStateOf(false) }

    LaunchedEffect(roomCode) { viewModel.initRoom(roomCode) }

    LaunchedEffect(uiState.sessionEnded) {
        if (uiState.sessionEnded) onSessionEnded(roomCode)
    }

    // Auto-ocultar el banner de resultado tras 3 segundos
    LaunchedEffect(uiState.lastAnswerCorrect) {
        if (uiState.lastAnswerCorrect != null) {
            delay(3000)
            viewModel.clearAnswerResult()
        }
    }

    Scaffold(

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

                    // Código de sala
                    item { RoomCodeCard(code = roomCode) }

                    // Usuarios en línea (presencia en tiempo real)
                    if (uiState.onlineUsers.isNotEmpty()) {
                        item {
                            OnlineUsersRow(onlineUsers = uiState.onlineUsers)
                        }
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
                        item { WaitingMessage() }
                    }

                    // Lista de participantes con scores
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
                                    text = "Ranking (${room.participants.size})",
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
                                    onSubtractPoint = { viewModel.addScore(roomCode, participant.userId, -1) }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
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
