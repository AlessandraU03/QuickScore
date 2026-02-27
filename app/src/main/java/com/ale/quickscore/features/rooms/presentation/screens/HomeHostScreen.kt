package com.ale.quickscore.features.rooms.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.auth.presentation.components.AuthButton
import com.ale.quickscore.features.rooms.presentation.viewmodels.RoomViewModel

@Composable
fun HomeHostScreen(
    onNavigateToRoom: (roomCode: String) -> Unit,
    onLogout: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.room) {
        uiState.room?.let { onNavigateToRoom(it.code) }
    }

    Scaffold(
        topBar = {
            TopAppBarHost(onLogout = onLogout)
        },
        bottomBar = {
            BottomNavigationHost()
        },
        containerColor = Color(0xFFF8F9FE)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Greeting
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¡Bienvenido, Host!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = Color(0xFF1D1B20)
                    )
                )
                Text(
                    text = "¿Listo para comenzar una nueva partida?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF79747E)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Action Card: Nueva Sala de Juego
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3EDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddBox,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF7C4DFF)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Nueva Sala de Juego",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Configura los parámetros, invita a tus jugadores y empieza a trackear puntuaciones en tiempo real.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF79747E)),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AuthButton(
                        text = "Crear sala",
                        onClick = { viewModel.createRoom() },
                        isLoading = uiState.isLoading
                    )

                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "INICIANDO SERVIDOR DE JUEGO...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF7C4DFF),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status Row
            StatusCard(
                label = "ESTADO SERVIDOR",
                value = "Operativo",
                icon = Icons.Default.SignalCellularAlt,
                iconColor = Color(0xFF4CAF50),
                backgroundColor = Color(0xFFE8F5E9)
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatusCard(
                label = "JUGADORES ONLINE",
                value = "1,240 activos",
                icon = Icons.Default.Groups,
                iconColor = Color(0xFF7C4DFF),
                backgroundColor = Color(0xFFF3EDFF)
            )

            uiState.error?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF79747E),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1D1B20)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarHost(onLogout: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF7C4DFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddBox,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "QuickScore",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        actions = {
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = Color(0xFF49454F),
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun BottomNavigationHost() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7C4DFF),
                selectedTextColor = Color(0xFF7C4DFF),
                indicatorColor = Color(0xFFF3EDFF)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
            label = { Text("Salas") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("Historial") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Ajustes") }
        )
    }
}
