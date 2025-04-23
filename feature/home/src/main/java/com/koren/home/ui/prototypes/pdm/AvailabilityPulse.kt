package com.koren.home.ui.prototypes.pdm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // Assuming Coil is used for image loading
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined

// --- Data Models (Firebase-friendly structure) ---

// Represents a user's current availability status
data class UserAvailability(
    val userId: String = "",
    val status: AvailabilityStatus = AvailabilityStatus.UNKNOWN,
    val nextBusyTimestamp: Long? = null, // Timestamp of the next scheduled busy event/task
    val nextBusyDescription: String? = null // Brief description of the next busy item
)

enum class AvailabilityStatus {
    FREE, BUSY, IN_TRANSIT, UNKNOWN, LIKELY_LEAVING_SOON // LIKELY_LEAVING_SOON from PM idea
}

// Placeholder for User data (assuming it exists elsewhere)
data class User(
    val id: String = "",
    val displayName: String = "",
    val profilePictureUrl: String? = null
)


// --- Composable Prototypes ---

/**
 * Prototype for the Family "Availability Pulse" card/section.
 * This could be placed on the home screen.
 */
@Composable
fun FamilyAvailabilityPulsePrototype(
    familyMembersAvailability: List<UserAvailability>,
    getDisplayName: (String) -> String = { "User $it" }, // Placeholder to get user display name
    getProfilePictureUrl: (String) -> String? = { null } // Placeholder to get user profile picture
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Padding to match home screen cards
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Family Availability Pulse", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(familyMembersAvailability) { availability ->
                    FamilyMemberAvailabilityItem(
                        availability = availability,
                        displayName = getDisplayName(availability.userId),
                        profilePictureUrl = getProfilePictureUrl(availability.userId)
                    )
                }
            }
        }
    }
}

@Composable
fun FamilyMemberAvailabilityItem(
    availability: UserAvailability,
    displayName: String,
    profilePictureUrl: String?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            // Profile Picture
            AsyncImage(
                model = profilePictureUrl,
                contentDescription = "$displayName's profile picture",
                modifier = Modifier
                    .size(56.dp) // Slightly larger size
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            // Status Indicator (small colored dot or icon)
            val statusColor = when (availability.status) {
                AvailabilityStatus.FREE -> Color.Green
                AvailabilityStatus.BUSY -> Color.Red
                AvailabilityStatus.IN_TRANSIT -> Color.Yellow
                AvailabilityStatus.LIKELY_LEAVING_SOON -> MaterialTheme.colorScheme.primary // Highlight color
                AvailabilityStatus.UNKNOWN -> Color.Gray
            }
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                    .align(Alignment.BottomEnd) // Position at bottom end
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(displayName, style = MaterialTheme.typography.labelSmall)
        // Optional: Show next busy time/description if status is not FREE
        if (availability.status != AvailabilityStatus.FREE && availability.nextBusyTimestamp != null) {
            val timeUntil = (availability.nextBusyTimestamp - System.currentTimeMillis()) / 1000 / 60
            Text(
                text = if (timeUntil > 0) "Busy in $timeUntil min" else availability.status.name.replace("_", " "), // Simple status or time until
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(availability.status.name.replace("_", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewFamilyAvailabilityPulse() {
    KorenTheme {
        Surface {
            FamilyAvailabilityPulsePrototype(
                familyMembersAvailability = listOf(
                    UserAvailability(userId = "userA", status = AvailabilityStatus.FREE),
                    UserAvailability(userId = "userB", status = AvailabilityStatus.BUSY, nextBusyTimestamp = System.currentTimeMillis() + 1800000, nextBusyDescription = "Meeting"), // Busy in 30 mins
                    UserAvailability(userId = "userC", status = AvailabilityStatus.IN_TRANSIT),
                    UserAvailability(userId = "userD", status = AvailabilityStatus.LIKELY_LEAVING_SOON, nextBusyTimestamp = System.currentTimeMillis() + 300000, nextBusyDescription = "Practice"), // Leaving in 5 mins
                    UserAvailability(userId = "userE", status = AvailabilityStatus.UNKNOWN)
                ),
                getDisplayName = { userId -> "User ${userId.last()}" }, // Mock display name
                getProfilePictureUrl = { userId -> "https://placehold.co/60x60/${if (userId.last() == 'A') "FF0000" else "0000FF"}/FFFFFF?text=${userId.last()}" } // Mock images
            )
        }
    }
}