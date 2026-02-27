package com.ale.quickscore.features.rooms.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.quickscore.features.auth.presentation.components.AuthButton
import com.ale.quickscore.features.rooms.presentation.viewmodels.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    roomCode: String,
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(roomCode) { viewModel.loadRanking(roomCode) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "QuickScore",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7C4DFF))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Trophy Icon Section
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3EDFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF7C4DFF)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Resultado Final",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            color = Color(0xFF1D1B20)
                        )
                    )

                    Text(
                        text = "¡Increíble desempeño hoy!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF79747E)
                        )
                    )

                    Spacer(modifier = Modifier.height(48.dp))
                }

                // Podium Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val top3 = uiState.ranking.take(3)
                        
                        // 2nd Place (Left)
                        if (top3.size >= 2) {
                            PodiumItem(
                                name = top3[1].name,
                                score = top3[1].score,
                                positionLabel = "2ND",
                                medalColor = Color(0xFFB0BEC5), // Silver
                                containerColor = Color(0xFFE3F2FD),
                                modifier = Modifier.weight(1f),
                                isWinner = false
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        // 1st Place (Center)
                        if (top3.isNotEmpty()) {
                            PodiumItem(
                                name = top3[0].name,
                                score = top3[0].score,
                                positionLabel = "WINNER",
                                medalColor = Color(0xFFFFD54F), // Gold
                                containerColor = Color(0xFFF3EDFF),
                                modifier = Modifier.weight(1.2f),
                                isWinner = true
                            )
                        }

                        // 3rd Place (Right)
                        if (top3.size >= 3) {
                            PodiumItem(
                                name = top3[2].name,
                                score = top3[2].score,
                                positionLabel = "3RD",
                                medalColor = Color(0xFFDCAE96), // Bronze
                                containerColor = Color(0xFFFFF3E0),
                                modifier = Modifier.weight(1f),
                                isWinner = false
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }

                item {
                    Text(
                        text = "Ranking General",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        ),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Rest of the Ranking
                val remainingRanking = uiState.ranking.drop(3)
                itemsIndexed(remainingRanking) { index, item ->
                    RankingRowItem(
                        position = index + 4,
                        name = item.name,
                        score = item.score
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    AuthButton(
                        text = "Volver al inicio",
                        onClick = onBack,
                        isLoading = false,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PodiumItem(
    name: String,
    score: Int,
    positionLabel: String,
    medalColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    isWinner: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isWinner) 150.dp else 120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Medal Placeholder
                Box(
                    modifier = Modifier
                        .size(if (isWinner) 40.dp else 32.dp)
                        .clip(CircleShape)
                        .background(medalColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (positionLabel == "WINNER") "1" else if (positionLabel == "2ND") "2" else "3",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isWinner) 18.sp else 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = "$score pts",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7C4DFF),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = positionLabel,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isWinner) Color(0xFF7C4DFF) else Color(0xFF79747E)
            )
        )
    }
}

@Composable
fun RankingRowItem(
    position: Int,
    name: String,
    score: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = position.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF79747E)
                ),
                modifier = Modifier.width(24.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF7C4DFF),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}
