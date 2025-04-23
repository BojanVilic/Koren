@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.prototypes.pdm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined
import java.util.Date // Using java.util.Date for simplicity with timestamp

// --- Data Models (Firebase-friendly structure) ---

// Represents an entry in the Family Brain knowledge base
data class KnowledgeEntry(
    val id: String? = null, // Firebase key
    val title: String = "",
    val content: String = "", // The main content (could be text, JSON for structured data)
    val type: String = "note", // e.g., "note", "password", "contact", "file_link"
    val createdByUserId: String = "",
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val sharedWithUserIds: List<String> = emptyList() // Users explicitly shared with (if not shared with whole family)
)


// --- Composable Prototypes ---

/**
 * Prototype screen for listing Family Brain Knowledge Entries.
 */
@Composable
fun KnowledgeEntryListScreenPrototype(
    entries: List<KnowledgeEntry>,
    onAddEntryClick: () -> Unit,
    onViewEntryClick: (KnowledgeEntry) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Family Brain") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntryClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Knowledge Entry")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Knowledge Entries", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            if (entries.isEmpty()) {
                item { Text("No knowledge entries added yet.") }
            } else {
                items(entries) { entry ->
                    KnowledgeEntryListItem(entry = entry, onClick = onViewEntryClick)
                }
            }
        }
    }
}

@Composable
fun KnowledgeEntryListItem(entry: KnowledgeEntry, onClick: (KnowledgeEntry) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(entry) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon based on entry type
            val entryIcon = when (entry.type) {
                "password" -> Icons.Default.Lock
                "contact" -> Icons.Default.Person
                "file_link" -> Icons.Default.Create
                else -> Icons.Default.ShoppingCart // Default for "note"
            }
            Icon(entryIcon, contentDescription = "${entry.type} icon", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)

            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(entry.title, style = MaterialTheme.typography.titleSmall)
                Text("Type: ${entry.type.capitalize()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = "View Entry", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}

/**
 * Prototype screen for viewing a single Family Brain Knowledge Entry.
 */
@Composable
fun KnowledgeEntryViewScreenPrototype(
    entry: KnowledgeEntry,
    onEditEntryClick: (KnowledgeEntry) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entry.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditEntryClick(entry) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Entry")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Type: ${entry.type.capitalize()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Content:", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            // Display content based on type - simple text for now
            Text(entry.content, style = MaterialTheme.typography.bodyMedium)

            // TODO: Add specific rendering for different types (e.g., password with hide/show, contact details)

            Spacer(Modifier.weight(1f)) // Push created info to bottom
            Text("Created by User ${entry.createdByUserId} on ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(entry.createdAtTimestamp))}", // Replace with user name
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewKnowledgeEntryListScreen() {
    KorenTheme {
        Surface {
            KnowledgeEntryListScreenPrototype(
                entries = listOf(
                    KnowledgeEntry(id = "k1", title = "Home Wi-Fi Password", type = "password", content = "MySecretPassword123"),
                    KnowledgeEntry(id = "k2", title = "Emergency Contact - Dr. Smith", type = "contact", content = "Phone: 555-1234"),
                    KnowledgeEntry(id = "k3", title = "Kids' School Schedule Link", type = "file_link", content = "http://example.com/schedule.pdf"),
                    KnowledgeEntry(id = "k4", title = "Notes about Dog's Medication", type = "note", content = "Give twice a day with food.")
                ),
                onAddEntryClick = {},
                onViewEntryClick = {}
            )
        }
    }
}

@ThemePreview
@Composable
fun PreviewKnowledgeEntryViewScreen() {
    KorenTheme {
        Surface {
            KnowledgeEntryViewScreenPrototype(
                entry = KnowledgeEntry(
                    id = "k1",
                    title = "Home Wi-Fi Password",
                    type = "password",
                    content = "MySecretPassword123! (Remember the exclamation mark)",
                    createdByUserId = "userA",
                    createdAtTimestamp = System.currentTimeMillis() - 86400000
                ),
                onEditEntryClick = {},
                onBackClick = {}
            )
        }
    }
}