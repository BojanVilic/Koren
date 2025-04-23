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

// --- Data Models (Firebase-friendly structure) ---

// Represents a rule for proactive check-ins
data class CheckInRule(
    val id: String? = null, // Firebase key
    val userId: String = "", // User this rule applies to
    val geofenceId: String? = null, // Geofence related to the rule (e.g., "at school", "not at home")
    val timeCondition: String? = null, // e.g., "after_time:15:00", "before_time:09:00", "during_time:08:00-16:00"
    val activityCondition: String? = null, // e.g., "no_movement_for:30m", "not_checked_in_task:taskId"
    val checkInMessage: String = "Hey, checking in! Everything okay?",
    val noResponseAction: String = "alert_parents", // e.g., "alert_parents", "send_notification_to:userId"
    val isActive: Boolean = true
)

// --- Composable Prototypes ---

/**
 * Prototype screen for managing Proactive Check-in Rules.
 */
@Composable
fun CheckInRulesScreenPrototype(
    rules: List<CheckInRule>,
    onAddRuleClick: () -> Unit,
    onEditRuleClick: (CheckInRule) -> Unit,
    getUserName: (String) -> String = { "User $it" }, // Placeholder
    getGeofenceName: (String) -> String = { "Geofence $it" } // Placeholder
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Proactive Check-in Rules") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRuleClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Rule")
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
            item { Text("Rules", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            if (rules.isEmpty()) {
                item { Text("No check-in rules set up yet.") }
            } else {
                items(rules) { rule ->
                    CheckInRuleItem(rule = rule, onEditClick = onEditRuleClick, getUserName = getUserName, getGeofenceName = getGeofenceName)
                }
            }
        }
    }
}

@Composable
fun CheckInRuleItem(
    rule: CheckInRule,
    onEditClick: (CheckInRule) -> Unit,
    getUserName: (String) -> String,
    getGeofenceName: (String) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onEditClick(rule) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Settings, contentDescription = "Rule", tint = MaterialTheme.colorScheme.primary)
                Text("Rule for: ${getUserName(rule.userId)}", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.weight(1f))
                Switch(checked = rule.isActive, onCheckedChange = { /* TODO: Implement toggle */ })
            }
            Spacer(Modifier.height(4.dp))
            rule.geofenceId?.let {
                Text("Location: ${getGeofenceName(it)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            rule.timeCondition?.let {
                Text("Time: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            rule.activityCondition?.let {
                Text("Activity: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Message: \"${rule.checkInMessage}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            Text("If no response: ${rule.noResponseAction.replace("_", " ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewCheckInRulesScreen() {
    KorenTheme {
        Surface {
            CheckInRulesScreenPrototype(
                rules = listOf(
                    CheckInRule(id = "r1", userId = "userB", geofenceId = "school", timeCondition = "after_time:15:30", noResponseAction = "alert_parents"),
                    CheckInRule(id = "r2", userId = "userC", geofenceId = "home", activityCondition = "no_movement_for:60m", noResponseAction = "send_notification_to:userA", checkInMessage = "Looks quiet at home, are you there?")
                ),
                onAddRuleClick = {},
                onEditRuleClick = {}
            )
        }
    }
}