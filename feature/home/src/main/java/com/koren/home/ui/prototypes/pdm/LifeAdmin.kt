package com.koren.home.ui.prototypes.pdm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koren.designsystem.icon.Event
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.Task
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined

// --- Data Models (Firebase-friendly structure) ---

// Represents a contextual suggestion
data class Suggestion(
    val id: String? = null, // Firebase key
    val type: String = "", // e.g., "task_suggestion", "event_suggestion", "communication_suggestion"
    val text: String = "", // The suggestion message
    val icon: String? = null, // Icon name or identifier
    val action: String? = null, // Action to take when clicked (e.g., "open_create_task", "open_chat_with:userId")
    val relevantEntityId: String? = null // ID of related entity (task, user, geofence)
)

// --- Composable Prototypes ---

/**
 * Prototype for a Contextual Suggestion Card UI element.
 * This could appear on the home screen, map screen, etc.
 */
@Composable
fun ContextualSuggestionCardPrototype(
    suggestion: Suggestion,
    onSuggestionClick: (Suggestion) -> Unit,
    onDismissSuggestion: (Suggestion) -> Unit // To dismiss the suggestion
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Add padding around the card
            .clickable { onSuggestionClick(suggestion) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Slightly less padding inside
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Suggestion Icon (using a placeholder)
            val suggestionIcon = when (suggestion.icon) {
                "task" -> KorenIcons.Task
                "event" -> KorenIcons.Event
                "chat" -> Icons.Default.Email
                "location" -> Icons.Default.LocationOn
                else -> Icons.Default.ShoppingCart // Default suggestion icon
            }
            Icon(
                suggestionIcon,
                contentDescription = "Suggestion Icon",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Suggestion:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(suggestion.text, style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = { onDismissSuggestion(suggestion) }) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss Suggestion", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewContextualSuggestionCard() {
    KorenTheme {
        Surface {
            Column { // Use a Column to preview multiple suggestions
                ContextualSuggestionCardPrototype(
                    suggestion = Suggestion(
                        id = "s1",
                        type = "task_suggestion",
                        text = "Looks like you're near the grocery store. Add 'Buy Milk' to the list?",
                        icon = "location",
                        action = "open_create_task",
                        relevantEntityId = "grocery_geofence_id"
                    ),
                    onSuggestionClick = {},
                    onDismissSuggestion = {}
                )
                ContextualSuggestionCardPrototype(
                    suggestion = Suggestion(
                        id = "s2",
                        type = "event_suggestion",
                        text = "It's Friday evening. Suggest planning a family movie night?",
                        icon = "event",
                        action = "open_create_event"
                    ),
                    onSuggestionClick = {},
                    onDismissSuggestion = {}
                )
            }
        }
    }
}