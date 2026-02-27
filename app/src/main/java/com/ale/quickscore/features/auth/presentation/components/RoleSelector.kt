package com.ale.quickscore.features.auth.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        RadioButton(
            selected = selectedRole == "participant",
            onClick = { onRoleSelected("participant") }
        )
        Text(
            text = "Participante",
            modifier = Modifier.padding(end = 24.dp)
        )
        RadioButton(
            selected = selectedRole == "host",
            onClick = { onRoleSelected("host") }
        )
        Text("Host")
    }
}