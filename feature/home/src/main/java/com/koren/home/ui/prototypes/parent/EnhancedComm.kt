package com.koren.home.ui.prototypes.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.util.Date // Using java.util.Date for simplicity with timestamp

// --- Data Models (Firebase-friendly structure) ---

// Represents a chat message
data class ChatMessage(
    val id: String? = null, // Firebase key
    val senderUserId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val text: String? = null,
    val mediaUrl: String? = null, // URL to shared media (photo, video)
    val isAnnouncement: Boolean = false // Flag for announcements
)

// Represents a quick "Pick Me Up" request
data class PickUpRequest(
    val id: String? = null, // Firebase key
    val requesterUserId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isResolved: Boolean = false
)


// --- Composable Prototypes ---

/**
 * Prototype for a single Chat Message item with enhanced styling.
 */
@Composable
fun ChatMessageItemPrototype(
    message: ChatMessage,
    isCurrentUser: Boolean, // To style messages differently
    senderName: String, // Display name of the sender
    senderImageUrl: String? // Profile picture of the sender
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isCurrentUser) {
            // Sender profile picture
            AsyncImage(
                model = senderImageUrl,
                contentDescription = "$senderName's profile picture",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom) // Align to the bottom of the message bubble
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder background
            )
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false) // Prevent message bubble from taking full width
        ) {
            if (!isCurrentUser) {
                Text(senderName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 4.dp))
            }
            Card(
                shape = RoundedCornerShape(
                    topStart = if (isCurrentUser) 12.dp else 4.dp,
                    topEnd = if (isCurrentUser) 4.dp else 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    message.text?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                    message.mediaUrl?.let {
                        // Placeholder for media display (e.g., AsyncImage for photos)
                        // In a real app, you'd load the image here
                        Box( // Placeholder Box for image
                            modifier = Modifier
                                .size(150.dp)
                                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Media Placeholder",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp).align(Alignment.Center)
                            )
                        }
                        // AsyncImage(model = it, contentDescription = "Shared media", modifier = Modifier.size(150.dp).clip(RoundedCornerShape(8.dp))) // Example with Coil
                    }
                }
            }
            Text(
                text = java.text.SimpleDateFormat("HH:mm").format(Date(message.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (isCurrentUser) {
            Spacer(Modifier.width(8.dp))
            // Current user profile picture (optional, could be on the other side or not shown)
            AsyncImage(
                model = senderImageUrl, // Use current user's image here
                contentDescription = "$senderName's profile picture",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom) // Align to the bottom of the message bubble
                    .background(MaterialTheme.colorScheme.primaryContainer) // Placeholder background
            )
        }
    }
}

/**
 * Prototype for an Announcement item in the chat or a separate feed.
 */
@Composable
fun AnnouncementItemPrototype(announcement: ChatMessage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp), // Add horizontal padding
        shape = RoundedCornerShape(12.dp), // Rounded corners
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), // Slightly more prominent background
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Notifications, contentDescription = "Announcement", tint = MaterialTheme.colorScheme.primary)
                Text("ANNOUNCEMENT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) // Slightly larger label
            }
            Spacer(Modifier.height(8.dp)) // Increased spacing
            announcement.text?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp)) // Increased spacing
            Text(
                text = "Sent by User ${announcement.senderUserId} at ${java.text.SimpleDateFormat("MMM dd, HH:mm").format(Date(announcement.timestamp))}", // Replace with sender name
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/**
 * Prototype for the "Pick Me Up" button with enhanced styling.
 */
@Composable
fun PickMeUpButtonPrototype(onPickMeUpClick: () -> Unit) {
    Button(
        onClick = onPickMeUpClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // Standard button height
        shape = RoundedCornerShape(12.dp), // Rounded corners
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp) // Add some elevation
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.LocationOn, contentDescription = "Pick Me Up")
            Text("Send Pick Me Up Request", style = MaterialTheme.typography.bodyMedium) // Match body medium text style
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewChatMessageItem() {
    KorenTheme {
        Surface {
            Column(Modifier.padding(8.dp)) {
                ChatMessageItemPrototype(
                    message = ChatMessage(senderUserId = "userA", text = "Hey family, what's for dinner?", timestamp = System.currentTimeMillis() - 120000),
                    isCurrentUser = false,
                    senderName = "User A",
                    senderImageUrl = "https://placehold.co/40x40"
                )
                ChatMessageItemPrototype(
                    message = ChatMessage(senderUserId = "userB", text = "Pizza tonight!", timestamp = System.currentTimeMillis() - 60000),
                    isCurrentUser = true,
                    senderName = "User B", // This would be the current user's name
                    senderImageUrl = "https://placehold.co/40x40" // Current user's image
                )
                ChatMessageItemPrototype(
                    message = ChatMessage(senderUserId = "userA", mediaUrl = "https://example.com/photo.jpg", timestamp = System.currentTimeMillis() - 30000),
                    isCurrentUser = false,
                    senderName = "User A",
                    senderImageUrl = "https://placehold.co/40x40"
                )
            }
        }
    }
}

@ThemePreview
@Composable
fun PreviewAnnouncementItem() {
    KorenTheme {
        Surface {
            AnnouncementItemPrototype(
                announcement = ChatMessage(senderUserId = "userA", text = "Don't forget, family meeting at 7 PM!", timestamp = System.currentTimeMillis(), isAnnouncement = true)
            )
        }
    }
}

@ThemePreview
@Composable
fun PreviewPickMeUpButton() {
    KorenTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            PickMeUpButtonPrototype(onPickMeUpClick = {})
        }
    }
}