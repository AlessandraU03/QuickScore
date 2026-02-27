package com.ale.quickscore.features.rooms.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.questions.presentation.components.AnswerResultBanner
import com.ale.quickscore.features.questions.presentation.screens.LaunchQuestionSheet
import com.ale.quickscore.features.rooms.domain.entities.Participant
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.presentation.components.ConnectionStatusBadge
import com.ale.quickscore.features.rooms.presentation.viewmodels.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    roomCode: String,
    onSessionEnded: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRanking: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isHost = uiState.room?.hostId == viewModel.getCurrentUserId()

    LaunchedEffect(roomCode) { viewModel.initRoom(roomCode) }

    LaunchedEffect(uiState.sessionEnded) {
        if (uiState.sessionEnded) onSessionEnded(roomCode)
    }

    // Modal para expulsión
    if (uiState.showKickDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onKickDismiss() },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(48.dp)) },
            title = { Text("¿Confirmar expulsión?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción eliminará a @${uiState.kickTargetName} de la sala.", textAlign = TextAlign.Center) },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmKick() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                ) { Text("Expulsar Usuario") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onKickDismiss() }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(28.dp)
        )
    }

    // Decidir qué vista mostrar: Si hay pregunta activa, todos ven la vista de pregunta
    if (uiState.activeQuestion != null) {
        ParticipantQuestionView(
            uiState = uiState,
            viewModel = viewModel,
            onNavigateToHome = onNavigateToHome,
            onNavigateToRanking = onNavigateToRanking,
            isHost = isHost
        )
    } else {
        RoomManagementView(
            roomCode = roomCode,
            isHost = isHost,
            uiState = uiState,
            viewModel = viewModel,
            onNavigateToHome = onNavigateToHome,
            onNavigateToRanking = onNavigateToRanking
        )
    }

    if (uiState.showLaunchSheet) {
        LaunchQuestionSheet(
            roomCode = roomCode,
            onDismiss = { viewModel.toggleLaunchSheet(false) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementView(
    roomCode: String,
    isHost: Boolean,
    uiState: RoomUIState,
    viewModel: RoomViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRanking: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Sala", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = { ConnectionStatusBadge(isConnected = uiState.isConnected) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "SALAS",
                onHomeClick = onNavigateToHome,
                onRankingClick = onNavigateToRanking
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { RoomCodeCard(roomCode) }
            item { StaffSection() }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val participantCount = uiState.onlineUsers.size.takeIf { it > 0 } ?: uiState.room?.participants?.size ?: 0
                    Text("PARTICIPANTES ($participantCount)", fontWeight = FontWeight.Bold)
                    if (isHost) Text("Gestionar", color = Color(0xFF79747E), modifier = Modifier.clickable { })
                }
            }
            
            val displayParticipants = if (uiState.onlineUsers.isNotEmpty()) {
                uiState.onlineUsers.map { Participant(it.userId, it.name, 0) } 
            } else {
                uiState.room?.participants ?: emptyList()
            }

            items(displayParticipants) { participant ->
                ParticipantDetailItem(participant, isHost, viewModel, roomCode)
            }
            
            item {
                if (isHost) {
                    Button(
                        onClick = {
                            if (!uiState.sessionStarted) viewModel.startRoom(roomCode)
                            else viewModel.toggleLaunchSheet(true)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                    ) {
                        Text(if (!uiState.sessionStarted) "Iniciar sesión" else "Lanzar pregunta", fontWeight = FontWeight.Bold)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantQuestionView(
    uiState: RoomUIState,
    viewModel: RoomViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRanking: () -> Unit,
    isHost: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Trivia Master Pro", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("SALA #${uiState.room?.code}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome, modifier = Modifier.background(Color(0xFFF3EDFF), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF7C4DFF))
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Info, null, tint = Color(0xFF7C4DFF) ) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = "JUEGO",
                onHomeClick = onNavigateToHome,
                onRankingClick = onNavigateToRanking
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resultado de la respuesta
            AnimatedVisibility(
                visible = uiState.lastAnswerCorrect != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AnswerResultBanner(
                    isCorrect = uiState.lastAnswerCorrect ?: false,
                    pointsEarned = uiState.lastAnswerPoints,
                    message = uiState.lastAnswerMessage,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = uiState.activeQuestion?.text ?: "",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 30.sp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isHost) {
                // VISTA DEL HOST: Botón para cerrar pregunta
                Button(
                    onClick = { viewModel.closeQuestion() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                ) {
                    Text("FINALIZAR PREGUNTA", fontWeight = FontWeight.Bold)
                }
            } else {
                // VISTA DEL PARTICIPANTE: Answer Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("TU RESPUESTA", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF49454F)))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.currentAnswer,
                        onValueChange = { viewModel.onCurrentAnswerChange(it) },
                        placeholder = { Text("Escribe tu respuesta aquí...", color = Color(0xFF948F99)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEADDFF),
                            focusedBorderColor = Color(0xFF7C4DFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.submitAnswer() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    enabled = !uiState.isAnswering && uiState.currentAnswer.isNotBlank()
                ) {
                    if (uiState.isAnswering) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ENVIAR RESPUESTA", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                
                // Mostrar error si falla el envío
                uiState.error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Ranking
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Leaderboard, null, tint = Color(0xFF7C4DFF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ranking en Vivo", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Text("${uiState.onlineUsers.size} JUGADORES", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF79747E)))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(uiState.ranking.take(3)) { index, item ->
                    LiveRankingItem(index + 1, item, isMe = item.userId == viewModel.getCurrentUserId())
                }
            }
        }
    }
}

@Composable
fun LiveRankingItem(pos: Int, item: RankingItem, isMe: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isMe) Color(0xFFF3EDFF) else Color.White),
        border = if (isMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F9))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(if (isMe) Color(0xFF7C4DFF) else Color(0xFFF8F9FE), CircleShape), contentAlignment = Alignment.Center) {
                Text(pos.toString(), color = if (isMe) Color.White else Color(0xFF79747E), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(if (isMe) "Tú (${item.name})" else item.name, fontWeight = FontWeight.Bold)
                if (isMe) Text("RACHA: 3 🔥", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF9800), fontWeight = FontWeight.Bold))
            }
            Text("${item.score} pts", fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C4DFF))
        }
    }
}

@Composable
fun RoomCodeCard(code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("CÓDIGO DE ACCESO", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF79747E)))
                    Text("#$code", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C4DFF)))
                }
                IconButton(onClick = { }, modifier = Modifier.size(48.dp).background(Color(0xFF7C4DFF), RoundedCornerShape(12.dp))) {
                    Icon(Icons.Default.ContentCopy, null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFFD81B60), Color(0xFF1A237E))))) {
                Surface(modifier = Modifier.padding(16.dp).align(Alignment.BottomStart), color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text("VISTA DE SALA", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(color = Color.White))
                }
            }
        }
    }
}

@Composable
fun StaffSection() {
    Column {
        Text("ANFITRIÓN Y STAFF", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy((-12).dp)) {
            repeat(3) { Surface(modifier = Modifier.size(48.dp), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(2.dp, Color.White), color = Color(0xFFE0E0E0)) { } }
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, border = androidx.compose.foundation.BorderStroke(2.dp, Color.White), color = Color(0xFFF3EDFF)) {
                Box(contentAlignment = Alignment.Center) { Text("+2", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun ParticipantDetailItem(participant: Participant, isHost: Boolean, viewModel: RoomViewModel, roomCode: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F9))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color(0xFFFFF9C4)) { }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(participant.name, fontWeight = FontWeight.Bold)
                Text("Puntos: ${participant.score}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF79747E))
            }
            if (isHost) {
                Surface(color = Color(0xFFF3F4F9), shape = RoundedCornerShape(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.addScore(roomCode, participant.userId, -1) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp)) }
                        Text("${participant.score}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.addScore(roomCode, participant.userId, 1) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp)) }
                    }
                }
                IconButton(onClick = { viewModel.onKickRequest(participant.userId, participant.name) }) { Icon(Icons.Default.PersonRemove, null, tint = Color(0xFFE0E0E0)) }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            Triple("SALAS", Icons.Default.Home, onHomeClick),
            Triple("JUEGO", Icons.Default.Quiz, { }),
            Triple("RANKING", Icons.Default.EmojiEvents, onRankingClick),
            Triple("PERFIL", Icons.Default.Person, { })
        )
        items.forEach { (label, icon, onClick) ->
            NavigationBarItem(
                selected = selectedItem == label,
                onClick = onClick,
                icon = { Icon(icon, null) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF7C4DFF),
                    selectedTextColor = Color(0xFF7C4DFF),
                    indicatorColor = Color(0xFFF3EDFF)
                )
            )
        }
    }
}
