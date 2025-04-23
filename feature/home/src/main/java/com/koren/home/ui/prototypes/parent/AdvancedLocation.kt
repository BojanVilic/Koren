@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.prototypes.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined in your designsystem module
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined in your designsystem module

// --- Data Models (Firebase-friendly structure) ---

// Represents a defined geographic area (Geofence)
data class Geofence(
    val id: String? = null, // Firebase key
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Double = 0.0, // in meters
    val createdByUserId: String = ""
)

// Settings for alerts related to a Geofence
data class GeofenceAlertSettings(
    val id: String? = null, // Firebase key
    val geofenceId: String = "",
    val userId: String = "", // User whose movement triggers the alert
    val notifyUserIds: List<String> = emptyList(), // Users to notify
    val notifyOnEntry: Boolean = true,
    val notifyOnExit: Boolean = true,
    val timeWindowStart: String? = null, // e.g., "08:00"
    val timeWindowEnd: String? = null, // e.g., "17:00"
    val repeatDays: List<Int> = emptyList() // e.g., [1, 2, 3] for Mon, Tue, Wed
)

// Represents an entry in the location history
data class LocationHistoryEntry(
    val id: String? = null, // Firebase key
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val eventType: String? = null, // e.g., "geofence_entry", "geofence_exit", "location_update"
    val geofenceId: String? = null // Link to geofence if applicable
)

// --- Composable Prototypes ---

/**
 * Prototype screen for managing Geofences and Alert Settings with enhanced styling.
 */
@Composable
fun GeofenceManagementScreenPrototype(
    geofences: List<Geofence>,
    alertSettings: List<GeofenceAlertSettings>,
    onAddGeofenceClick: () -> Unit,
    onEditGeofenceClick: (Geofence) -> Unit,
    onEditAlertSettingsClick: (GeofenceAlertSettings) -> Unit,
    getUserName: (String) -> String = { "User $it" } // Placeholder to get user name
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Location Safety & Geofences") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGeofenceClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Geofence")
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
            item { Text("Saved Places (Geofences)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            items(geofences) { geofence ->
                GeofenceItem(geofence = geofence, onEditClick = onEditGeofenceClick)
            }
            item { Divider(Modifier.padding(vertical = 16.dp)) }
            item { Text("Alert Settings", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            items(alertSettings) { settings ->
                AlertSettingsItem(settings = settings, onEditClick = onEditAlertSettingsClick, getUserName = getUserName)
            }
        }
    }
}

@Composable
fun GeofenceItem(geofence: Geofence, onEditClick: (Geofence) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onEditClick(geofence) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder for a map preview or icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Geofence Location", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(geofence.name, style = MaterialTheme.typography.titleSmall)
                Text("Radius: ${geofence.radius}m", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit Geofence", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AlertSettingsItem(
    settings: GeofenceAlertSettings,
    onEditClick: (GeofenceAlertSettings) -> Unit,
    getUserName: (String) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onEditClick(settings) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder for user profile picture
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "User", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Alert for ${getUserName(settings.userId)}", style = MaterialTheme.typography.titleSmall)
                Text("at Geofence ID: ${settings.geofenceId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) // Link to geofence name?
                val triggerText = when {
                    settings.notifyOnEntry && settings.notifyOnExit -> "on Entry and Exit"
                    settings.notifyOnEntry -> "on Entry"
                    settings.notifyOnExit -> "on Exit"
                    else -> "No alerts set"
                }
                Text("Triggers: $triggerText", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Prototype screen for viewing Location History with enhanced styling.
 */
@Composable
fun LocationHistoryScreenPrototype(
    historyEntries: List<LocationHistoryEntry>,
    getUserName: (String) -> String = { "User $it" } // Placeholder to get user name
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Location History") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(historyEntries) { entry ->
                LocationHistoryItem(entry = entry, getUserName = getUserName)
            }
        }
    }
}

@Composable
fun LocationHistoryItem(entry: LocationHistoryEntry, getUserName: (String) -> String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder for user profile picture
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "User", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(getUserName(entry.userId), style = MaterialTheme.typography.titleSmall)
                val eventDescription = when (entry.eventType) {
                    "geofence_entry" -> "Entered Geofence ${entry.geofenceId}" // Link to geofence name?
                    "geofence_exit" -> "Exited Geofence ${entry.geofenceId}" // Link to geofence name?
                    else -> "Location Updated"
                }
                Text(eventDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // Could add a small map preview of the location
            }
            Column(horizontalAlignment = Alignment.End) {
                val timeAgo = (System.currentTimeMillis() - entry.timestamp) / 1000 / 60
                Text(
                    text = if (timeAgo < 60) "$timeAgo minutes ago" else "${timeAgo / 60} hours ago", // More robust time ago
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Icon indicating type of event (car, pin) - requires custom icons
                when(entry.eventType) {
                    "geofence_entry", "geofence_exit" -> Icon(Icons.Default.LocationOn, contentDescription = "Geofence Event", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> Icon(Icons.Default.Search, contentDescription = "Location Update", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewGeofenceManagementScreen() {
    KorenTheme {
        Surface {
            GeofenceManagementScreenPrototype(
                geofences = listOf(
                    Geofence(id = "1", name = "Home", latitude = 43.0, longitude = -79.0, radius = 100.0),
                    Geofence(id = "2", name = "School", latitude = 43.1, longitude = -79.1, radius = 200.0)
                ),
                alertSettings = listOf(
                    GeofenceAlertSettings(id = "s1", geofenceId = "1", userId = "userA", notifyUserIds = listOf("userB"), notifyOnEntry = true, notifyOnExit = false),
                    GeofenceAlertSettings(id = "s2", geofenceId = "2", userId = "userC", notifyUserIds = listOf("userA", "userB"), notifyOnEntry = true, notifyOnExit = true, timeWindowStart = "08:00", timeWindowEnd = "15:00")
                ),
                onAddGeofenceClick = {},
                onEditGeofenceClick = {},
                onEditAlertSettingsClick = {}
            )
        }
    }
}

@ThemePreview
@Composable
fun PreviewLocationHistoryScreen() {
    KorenTheme {
        Surface {
            LocationHistoryScreenPrototype(
                historyEntries = listOf(
                    LocationHistoryEntry(userId = "userA", timestamp = System.currentTimeMillis() - 60000, eventType = "geofence_entry", geofenceId = "1"),
                    LocationHistoryEntry(userId = "userB", timestamp = System.currentTimeMillis() - 120000, eventType = "location_update"),
                    LocationHistoryEntry(userId = "userA", timestamp = System.currentTimeMillis() - 300000, eventType = "geofence_exit", geofenceId = "2"),
                    LocationHistoryEntry(userId = "userC", timestamp = System.currentTimeMillis() - 600000, eventType = "location_update")
                )
            )
        }
    }
}