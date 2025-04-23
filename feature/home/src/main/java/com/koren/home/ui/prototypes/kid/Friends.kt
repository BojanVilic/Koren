@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.prototypes.kid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Assuming Coil is used for image loading
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined

// --- Data Models (Firebase-friendly structure) ---

// Placeholder for User data (assuming it exists elsewhere)
data class User(
    val id: String = "",
    val displayName: String = "",
    val profilePictureUrl: String? = null,
    val isFriend: Boolean = false // Added flag for 'friend' relationship within family
)

// Simplified Location Status for kid view
data class SimpleLocationStatus(
    val userId: String = "",
    val status: String = "Unknown", // e.g., "Nearby", "At Home", "At School", "Away"
    val geofenceId: String? = null // Link to geofence if applicable
)


// --- Composable Prototypes ---

/**
 * Prototype screen for a simplified view of family members, highlighting friends and simple status.
 */
@Composable
fun KidFamilyListViewPrototype(
    familyMembers: List<User>,
    locationStatuses: List<SimpleLocationStatus>,
    onMemberClick: (User) -> Unit,
    onBackClick: () -> Unit,
    getGeofenceName: (String) -> String = { "Place $it" } // Placeholder
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Family & Friends") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Family Members", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            items(familyMembers) { member ->
                KidFamilyMemberItem(
                    user = member,
                    locationStatus = locationStatuses.find { it.userId == member.id },
                    onMemberClick = onMemberClick,
                    getGeofenceName = getGeofenceName
                )
            }
        }
    }
}

@Composable
fun KidFamilyMemberItem(
    user: User,
    locationStatus: SimpleLocationStatus?,
    onMemberClick: (User) -> Unit,
    getGeofenceName: (String) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onMemberClick(user) },
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
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "${user.displayName}'s profile picture",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop // Crop image to fit circle
                )
            }

            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(user.displayName, style = MaterialTheme.typography.titleSmall)
                    if (user.isFriend) {
                        Icon(Icons.Default.Star, contentDescription = "Friend", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary) // Star icon for friends
                    }
                }
                locationStatus?.let {
                    val statusText = when (it.status) {
                        "Nearby" -> "Nearby!"
                        "At Home" -> "At Home"
                        "At School" -> "At School"
                        "Away" -> "Away"
                        "At Saved Place" -> "At ${it.geofenceId?.let { getGeofenceName(it) } ?: "a saved place"}"
                        else -> "Status Unknown"
                    }
                    Text(statusText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } ?: Text("Status Unknown", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Member", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewKidFamilyListView() {
    KorenTheme {
        Surface {
            KidFamilyListViewPrototype(
                familyMembers = listOf(
                    User(id = "userA", displayName = "Mom", profilePictureUrl = "https://placehold.co/48x48/FF0000/FFFFFF?text=M"),
                    User(id = "userB", displayName = "Dad", profilePictureUrl = "https://placehold.co/48x48/0000FF/FFFFFF?text=D"),
                    User(id = "userC", displayName = "Sibling 1", profilePictureUrl = "https://placehold.co/48x48/00FF00/FFFFFF?text=S1"),
                    User(id = "userD", displayName = "Cousin Timmy", profilePictureUrl = "https://placehold.co/48x48/FFFF00/000000?text=CT", isFriend = true) // Friend within family
                ),
                locationStatuses = listOf(
                    SimpleLocationStatus(userId = "userA", status = "At Home", geofenceId = "home"),
                    SimpleLocationStatus(userId = "userB", status = "Away"),
                    SimpleLocationStatus(userId = "userC", status = "At School", geofenceId = "school"),
                    SimpleLocationStatus(userId = "userD", status = "Nearby")
                ),
                onMemberClick = {},
                onBackClick = {}
            )
        }
    }
}