@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.koren.home.ui.prototypes.kid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

// --- Data Models (Firebase-friendly structure) ---

// Represents a user's current profile customization selections
data class UserProfileCustomization(
    val userId: String? = null, // Firebase key
    val selectedIconId: String? = null,
    val selectedFrameId: String? = null,
    val selectedThemeId: String? = null
)

// Represents an available asset for profile customization
data class AvailableProfileAsset(
    val id: String? = null, // Firebase key
    val type: String = "icon", // e.g., "icon", "frame", "theme"
    val name: String = "",
    val pointsCost: Int = 0, // 0 means unlocked by default or via other means
    val previewUrl: String? = null // URL for preview image/icon
)


// --- Composable Prototypes ---

/**
 * Prototype screen for customizing user profile and app style.
 */
@Composable
fun ProfileCustomizationScreenPrototype(
    currentUserCustomization: UserProfileCustomization,
    availableAssets: List<AvailableProfileAsset>,
    currentPoints: Int,
    onAssetSelect: (AvailableProfileAsset) -> Unit, // Handle selection (unlocking if needed)
    onBackClick: () -> Unit,
    getUserProfilePictureUrl: (String) -> String? = { null } // Placeholder
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("My Profile Preview", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        // Placeholder for profile picture with selected icon/frame
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "User Icon", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            // TODO: Overlay selected icon and frame here
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("My Name", style = MaterialTheme.typography.titleSmall) // Placeholder for user name
                    }
                }
            }

            val icons = availableAssets.filter { it.type == "icon" }
            if (icons.isNotEmpty()) {
                item { Text("Profile Icons", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                        items(icons) { asset ->
                            ProfileAssetItem(
                                asset = asset,
                                isSelected = asset.id == currentUserCustomization.selectedIconId,
                                isUnlocked = currentPoints >= asset.pointsCost, // Simplified unlock logic
                                currentPoints = currentPoints,
                                onAssetClick = onAssetSelect
                            )
                        }
                    }
                }
            }

            val frames = availableAssets.filter { it.type == "frame" }
            if (frames.isNotEmpty()) {
                item { Text("Profile Frames", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                        items(frames) { asset ->
                            ProfileAssetItem(
                                asset = asset,
                                isSelected = asset.id == currentUserCustomization.selectedFrameId,
                                isUnlocked = currentPoints >= asset.pointsCost, // Simplified unlock logic
                                currentPoints = currentPoints,
                                onAssetClick = onAssetSelect
                            )
                        }
                    }
                }
            }

            val themes = availableAssets.filter { it.type == "theme" }
            if (themes.isNotEmpty()) {
                item { Text("App Themes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                        items(themes) { asset ->
                            ProfileAssetItem(
                                asset = asset,
                                isSelected = asset.id == currentUserCustomization.selectedThemeId,
                                isUnlocked = currentPoints >= asset.pointsCost, // Simplified unlock logic
                                currentPoints = currentPoints,
                                onAssetClick = onAssetSelect
                            )
                        }
                    }
                }
            }

            // TODO: Add sections for other customization types (e.g., sounds, animations)
        }
    }
}

@Composable
fun ProfileAssetItem(
    asset: AvailableProfileAsset,
    isSelected: Boolean,
    isUnlocked: Boolean, // Simplified: based on points
    currentPoints: Int,
    onAssetClick: (AvailableProfileAsset) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) { // Fixed width for items
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)) // Rounded corners for asset preview
                .background(
                    when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isUnlocked -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Dim if locked
                    }
                )
                .clickable { if (isUnlocked) onAssetClick(asset) /* TODO: Handle unlocking purchase flow */ },
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for asset preview (icon, color swatch, mini theme preview)
            when (asset.type) {
                "icon" -> Icon(Icons.Default.Face, contentDescription = asset.name, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isUnlocked) 1f else 0.5f))
                "frame" -> Icon(Icons.Default.Favorite, contentDescription = asset.name, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isUnlocked) 1f else 0.5f))
                "theme" -> Box(modifier = Modifier.size(40.dp).background(Color.Blue).clip(CircleShape)) // Simple color swatch preview
                else -> Icon(Icons.Default.Home, contentDescription = asset.name, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isUnlocked) 1f else 0.5f))
            }

            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(asset.name, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        if (!isUnlocked) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Points Cost", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                Text("${asset.pointsCost}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        } else if (isSelected) {
            Text("Selected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        } else {
            Spacer(Modifier.height(12.dp)) // Maintain spacing if unlocked but not selected
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewProfileCustomizationScreen() {
    KorenTheme {
        Surface {
            ProfileCustomizationScreenPrototype(
                currentUserCustomization = UserProfileCustomization(selectedIconId = "icon2", selectedThemeId = "theme1"),
                availableAssets = listOf(
                    AvailableProfileAsset(id = "icon1", type = "icon", name = "Robot", pointsCost = 0),
                    AvailableProfileAsset(id = "icon2", type = "icon", name = "Cat", pointsCost = 50),
                    AvailableProfileAsset(id = "icon3", type = "icon", name = "Rocket", pointsCost = 100),
                    AvailableProfileAsset(id = "frame1", type = "frame", name = "Stars", pointsCost = 0),
                    AvailableProfileAsset(id = "frame2", type = "frame", name = "Dots", pointsCost = 75),
                    AvailableProfileAsset(id = "theme1", type = "theme", name = "Ocean Blue", pointsCost = 0, previewUrl = "0077CC"),
                    AvailableProfileAsset(id = "theme2", type = "theme", name = "Forest Green", pointsCost = 120, previewUrl = "228B22")
                ),
                currentPoints = 80,
                onAssetSelect = {},
                onBackClick = {}
            )
        }
    }
}