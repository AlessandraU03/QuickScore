package com.ale.quickscore.features.rooms.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.questions.presentation.screens.LaunchQuestionSheet
import com.ale.quickscore.features.rooms.domain.entities.Participant
import com.ale.quickscore.features.rooms.domain.entities.RankingItem
import com.ale.quickscore.features.rooms.presentation.components.ConnectionStatusBadge
import com.ale.quickscore.features.rooms.presentation.viewmodels.RoomViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    roomCode: String,
    onSessionEnded: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isHost = uiState.room?.hostId == viewModel.getCurrentUserId()
    
    // Estado local para la pestaña seleccionada
    var selectedTab by remember { mutableStateOf("JUEGO") }

    BackHandler { onBack() }

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

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                "JUEGO" -> {
                    if (uiState.activeQuestion != null) {
                        QuestionView(isHost, uiState, viewModel, onBack)
                    } else {
                        RoomManagementView(roomCode, isHost, uiState, viewModel, onBack)
                    }
                }
                "RANKING" -> {
                    RankingView(uiState, viewModel, onBack)
                }
                "PERFIL" -> {
                    ProfileView(viewModel, onLogout, onBack)
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sección $selectedTab en desarrollo")
                    }
                }
            }
        }
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
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Detalle de Sala", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { 
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") 
                }
            },
            actions = { ConnectionStatusBadge(isConnected = uiState.isConnected) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { RoomCodeCard(roomCode) }
            item { StaffSection() }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PARTICIPANTES (${uiState.room?.participants?.size ?: 0})", fontWeight = FontWeight.Bold)
                    if (isHost) Text("Gestionar", color = Color(0xFF79747E), modifier = Modifier.clickable { })
                }
            }
            items(uiState.room?.participants ?: emptyList()) { participant ->
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
fun QuestionView(
    isHost: Boolean,
    uiState: RoomUIState,
    viewModel: RoomViewModel,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pregunta Activa", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("SALA #${uiState.room?.code}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold))
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFFF3EDFF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color(0xFF7C4DFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            actions = {
                if (isHost) {
                    IconButton(onClick = { viewModel.closeQuestion() }) {
                        Icon(Icons.Default.Close, "Cerrar Pregunta", tint = Color.Red)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Feedback de respuesta enviada
            AnimatedVisibility(
                visible = uiState.lastAnswerCorrect != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.lastAnswerCorrect == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.lastAnswerCorrect == true) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (uiState.lastAnswerCorrect == true) Color(0xFF4CAF50) else Color(0xFFEF5350)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (uiState.lastAnswerCorrect == true) 
                                "¡Correcto! +${uiState.lastAnswerPoints} pts" else "Respuesta incorrecta",
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.lastAnswerCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }

            // Question Card principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color(0xFFF3EDFF),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${uiState.activeQuestion?.points ?: 0}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF7C4DFF),
                                fontSize = 24.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = uiState.activeQuestion?.text ?: "",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 34.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!isHost) {
                // Participante responde
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.currentAnswer,
                        onValueChange = { viewModel.onCurrentAnswerChange(it) },
                        placeholder = { Text("Escribe tu respuesta...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color(0xFFEADDFF)
                        ),
                        enabled = !uiState.isAnswering && uiState.lastAnswerCorrect == null
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                        enabled = !uiState.isAnswering && uiState.currentAnswer.isNotBlank() && uiState.lastAnswerCorrect == null
                    ) {
                        if (uiState.isAnswering) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("ENVIAR RESPUESTA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            } else {
                // Host visualiza progreso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDFF))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Los estudiantes están respondiendo", fontWeight = FontWeight.Bold, color = Color(0xFF7C4DFF))
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = Color(0xFF7C4DFF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Ranking en vivo
            Text("TOP 3 RANKING", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
            Spacer(modifier = Modifier.height(16.dp))
            uiState.ranking.take(3).forEachIndexed { index, item ->
                LiveRankingItem(index + 1, item, isMe = item.userId == viewModel.getCurrentUserId())
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    viewModel: RoomViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Avatar Placeholder
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color(0xFFF3EDFF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(50.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Usuario Autenticado", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text("ID: ${viewModel.getCurrentUserId()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingView(
    uiState: RoomUIState,
    viewModel: RoomViewModel,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Resultado Final", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
        
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Trophy Icon (Figma style)
            Box(
                modifier = Modifier.size(100.dp).background(Color(0xFFF3EDFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(50.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Ranking General", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(uiState.ranking) { index, item ->
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
                if (isMe) Text("RACHA: 🔥", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF9800), fontWeight = FontWeight.Bold))
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
fun BottomNavigationBar(selectedItem: String, onItemSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            "JUEGO" to Icons.Default.Quiz, 
            "RANKING" to Icons.Default.EmojiEvents, 
            "CHAT" to Icons.Default.Chat, 
            "PERFIL" to Icons.Default.Person
        )
        items.forEach { (label, icon) ->
            NavigationBarItem(
                selected = selectedItem == label,
                onClick = { onItemSelected(label) },
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
