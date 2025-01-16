package com.koren.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koren.designsystem.models.ActionItem

@Composable
fun ActionButton(
    actionItem: ActionItem
) {
    OutlinedButton(
        onClick = actionItem.onClick,
        border = null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            actionItem.IconComposable()
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = actionItem.text,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}