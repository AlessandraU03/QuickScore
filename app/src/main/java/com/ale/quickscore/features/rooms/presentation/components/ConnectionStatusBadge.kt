package com.ale.quickscore.features.rooms.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionStatusBadge(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (isConnected)
                    Color(0xFF00FF87).copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.errorContainer
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (isConnected) Color(0xFF00FF87)
                    else MaterialTheme.colorScheme.error
                )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isConnected) "Conectado" else "Desconectado",
            style = MaterialTheme.typography.labelSmall,
            color = if (isConnected) Color(0xFF00FF87)
            else MaterialTheme.colorScheme.error
        )
    }
}