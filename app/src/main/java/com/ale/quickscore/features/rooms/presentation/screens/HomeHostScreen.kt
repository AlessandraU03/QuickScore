package com.ale.quickscore.features.rooms.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

    // Solo buscamos si hay sala al iniciar, pero NO navegamos automáticamente
    LaunchedEffect(Unit) {
        viewModel.checkLastActiveRoom()
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
                    text = "¡Hola, Profesor!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = Color(0xFF1D1B20)
                    )
                )
                Text(
                    text = "¿Qué deseas hacer hoy?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF79747E)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Retomar Sala Activa (Si existe)
            // Esta tarjeta aparecerá si checkLastActiveRoom encontró algo
            AnimatedVisibility(visible = uiState.room != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("SALA ACTIVA DETECTADA", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                            Text("#${uiState.room?.code}", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onNavigateToRoom(uiState.room!!.code) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF7C4DFF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("RETOMAR")
                        }
                    }
                }
            }

            // Main Action Card: Nueva Sala
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        text = "Crea una nueva sesión para tus alumnos.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF79747E)),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AuthButton(
                        text = "Crear nueva sala",
                        onClick = { 
                            // Al crear una nueva, sí queremos ir directamente a ella
                            viewModel.createRoom { code ->
                                onNavigateToRoom(code)
                            }
                        },
                        isLoading = uiState.isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            StatusCard(
                label = "ESTADO DEL SISTEMA",
                value = "En línea",
                icon = Icons.Default.CloudDone,
                iconColor = Color(0xFF4CAF50),
                backgroundColor = Color(0xFFE8F5E9)
            )

            uiState.error?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatusCard(label: String, value: String, icon: ImageVector, iconColor: Color, backgroundColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(backgroundColor), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarHost(onLogout: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("QuickScore Host", fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ExitToApp, "Salir")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun BottomNavigationHost() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.History, null) }, label = { Text("Historial") })
    }
}
