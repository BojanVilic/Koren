@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.prototypes.parent

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

// Represents an event from any source (internal or external)
data class CalendarEvent(
    val id: String? = null, // Firebase key
    val title: String = "",
    val description: String? = null,
    val startTimestamp: Long = 0L,
    val endTimestamp: Long = 0L,
    val isAllDay: Boolean = false,
    val sourceCalendarId: String = "internal", // e.g., "internal", "google_calendar_abc", "outlook_calendar_xyz"
    val creatorUserId: String? = null // User who created it (if internal)
)

// Represents a connected external calendar account
data class ExternalCalendarAccount(
    val id: String? = null, // Firebase key
    val userId: String = "", // User who connected the account
    val type: String = "", // e.g., "google", "outlook"
    val accountIdentifier: String = "", // e.g., email address
    val isSyncEnabled: Boolean = true
)

// Represents a rule for smart notifications
data class SmartNotificationRule(
    val id: String? = null, // Firebase key
    val userId: String = "", // User who set the rule
    val appliesToUserId: String? = null, // Rule applies to events for this user (or null for all)
    val eventKeywords: List<String> = emptyList(), // Keywords in event title/description
    val locationCondition: String? = null, // e.g., "is_at_saved_place:home", "is_not_at_saved_place:work"
    val timeOffsetMinutes: Int = 0, // e.g., -30 for 30 minutes before event
    val notificationMessage: String? = null // Custom message
)


// --- Composable Prototypes ---

/**
 * Prototype screen for managing connected external calendars with enhanced styling.
 */
@Composable
fun CalendarSyncSettingsScreenPrototype(
    connectedAccounts: List<ExternalCalendarAccount>,
    onConnectAccountClick: () -> Unit,
    onToggleSync: (ExternalCalendarAccount, Boolean) -> Unit,
    onRemoveAccount: (ExternalCalendarAccount) -> Unit,
    getUserName: (String) -> String = { "User $it" } // Placeholder
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar Sync Settings") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onConnectAccountClick) {
                Icon(Icons.Default.Add, contentDescription = "Connect Calendar Account")
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
            item { Text("Connected Accounts", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            if (connectedAccounts.isEmpty()) {
                item { Text("No external accounts connected yet.") }
            } else {
                items(connectedAccounts) { account ->
                    ExternalCalendarAccountItem(
                        account = account,
                        onToggleSync = onToggleSync,
                        onRemoveAccount = onRemoveAccount
                    )
                }
            }
        }
    }
}

@Composable
fun ExternalCalendarAccountItem(
    account: ExternalCalendarAccount,
    onToggleSync: (ExternalCalendarAccount, Boolean) -> Unit,
    onRemoveAccount: (ExternalCalendarAccount) -> Unit
) {
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
            // Icon based on calendar type
            val calendarIcon = when (account.type.toLowerCase()) {
                "google" -> Icons.Default.DateRange // Or a custom Google Calendar icon
                "outlook" -> Icons.Default.DateRange // Or a custom Outlook icon
                else -> Icons.Default.DateRange
            }
            Icon(calendarIcon, contentDescription = "${account.type} Calendar", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)

            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("${account.type.capitalize()} Calendar", style = MaterialTheme.typography.titleSmall)
                Text(account.accountIdentifier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sync", style = MaterialTheme.typography.labelMedium)
                Switch(
                    checked = account.isSyncEnabled,
                    onCheckedChange = { isEnabled -> onToggleSync(account, isEnabled) }
                )
                IconButton(onClick = { onRemoveAccount(account) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Account", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

/**
 * Prototype screen for setting up Smart Notification Rules with enhanced styling.
 */
@Composable
fun SmartNotificationRulesScreenPrototype(
    rules: List<SmartNotificationRule>,
    onAddRuleClick: () -> Unit,
    onEditRuleClick: (SmartNotificationRule) -> Unit,
    getUserName: (String) -> String = { "User $it" } // Placeholder
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Smart Notification Rules") })
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
            item { Text("Your Rules", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            if (rules.isEmpty()) {
                item { Text("No smart notification rules set up yet.") }
            } else {
                items(rules) { rule ->
                    SmartNotificationRuleItem(rule = rule, onEditClick = onEditRuleClick, getUserName = getUserName)
                }
            }
        }
    }
}

@Composable
fun SmartNotificationRuleItem(
    rule: SmartNotificationRule,
    onEditClick: (SmartNotificationRule) -> Unit,
    getUserName: (String) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onEditClick(rule) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rule for: ${rule.appliesToUserId?.let { getUserName(it) } ?: "Any Family Member"}", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            rule.eventKeywords.takeIf { it.isNotEmpty() }?.let {
                Text("Keywords: ${it.joinToString()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            rule.locationCondition?.let {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location Condition", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Condition: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) // Describe condition simply
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Notifications, contentDescription = "Notification Timing", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Notify ${if (rule.timeOffsetMinutes < 0) "${-rule.timeOffsetMinutes} mins before" else "${rule.timeOffsetMinutes} mins after"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            rule.notificationMessage?.let {
                Text("Message: \"$it\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit Rule", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End))
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewCalendarSyncSettingsScreen() {
    KorenTheme {
        Surface {
            CalendarSyncSettingsScreenPrototype(
                connectedAccounts = listOf(
                    ExternalCalendarAccount(id = "g1", userId = "userA", type = "google", accountIdentifier = "user.a@gmail.com", isSyncEnabled = true),
                    ExternalCalendarAccount(id = "o1", userId = "userA", type = "outlook", accountIdentifier = "user.a@outlook.com", isSyncEnabled = false)
                ),
                onConnectAccountClick = {},
                onToggleSync = { _, _ -> },
                onRemoveAccount = {}
            )
        }
    }
}

@ThemePreview
@Composable
fun PreviewSmartNotificationRulesScreen() {
    KorenTheme {
        Surface {
            SmartNotificationRulesScreenPrototype(
                rules = listOf(
                    SmartNotificationRule(id = "r1", userId = "userA", appliesToUserId = "userB", eventKeywords = listOf("practice"), locationCondition = "is_at_saved_place:home", timeOffsetMinutes = -45, notificationMessage = "Time to leave for practice!"),
                    SmartNotificationRule(id = "r2", userId = "userA", appliesToUserId = null, eventKeywords = listOf("appointment"), timeOffsetMinutes = -15)
                ),
                onAddRuleClick = {},
                onEditRuleClick = {}
            )
        }
    }
}