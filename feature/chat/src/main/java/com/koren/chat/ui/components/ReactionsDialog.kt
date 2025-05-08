package com.koren.chat.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun ReactionSelectionDialog(
    onDismissRequest: () -> Unit,
    onReactionSelected: (String) -> Unit
) {
    val reactions = listOf("👍", "❤️", "😂", "😮", "😢", "🙏")

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("React to message") },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                reactions.forEach { reaction ->
                    TextButton(onClick = { onReactionSelected(reaction) }) {
                        Text(reaction, fontSize = 24.sp)
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}