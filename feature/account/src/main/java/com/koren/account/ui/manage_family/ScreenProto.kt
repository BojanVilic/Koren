package com.koren.account.ui.manage_family

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.models.family.FamilyRole
import com.koren.common.models.user.UserData
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

// Placeholder for child-specific settings that a moderator would manage
data class ChildSettings(
    val childId: String,
    var screenTimeLimitsEnabled: Boolean = false,
    var contentFilteringEnabled: Boolean = false,
    var locationSharingEnabled: Boolean = false
    // Add more child-specific settings here
)

enum class ManageFamilyState {
    LIST_OVERVIEW,
    MEMBER_ACTIONS,
    CHILD_SETTINGS, // New state for child-specific settings
    SELECT_ROLE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFamilyBottomSheetContent(
    familyMembers: List<UserData>,
    onUpdateChildSettings: (ChildSettings) -> Unit, // New event for child settings
    onEditRole: (UserData, FamilyRole) -> Unit,
    onRemoveFamilyMember: (UserData) -> Unit,
    onDismiss: () -> Unit,
    onAddFamilyMember: () -> Unit,
    isLoading: Boolean = false
) {

    var currentScreenState by remember { mutableStateOf(ManageFamilyState.LIST_OVERVIEW) }
    var selectedFamilyMember by remember { mutableStateOf<UserData?>(null) }
    var currentChildSettings by remember { mutableStateOf<ChildSettings?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        )
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }

        AnimatedContent(
            targetState = currentScreenState,
            transitionSpec = {
                when (targetState) {
                    ManageFamilyState.MEMBER_ACTIONS, ManageFamilyState.CHILD_SETTINGS, ManageFamilyState.SELECT_ROLE ->
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(fadeOut())
                    ManageFamilyState.LIST_OVERVIEW ->
                        (fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
                }.using(SizeTransform(clip = false))
            }, label = "ManageFamilyTransition"
        ) { targetState ->
            when (targetState) {
                ManageFamilyState.LIST_OVERVIEW -> {
                    FamilyMemberListScreen(
                        familyMembers = familyMembers,
                        onMemberClick = { member ->
                            selectedFamilyMember = member
                            currentScreenState = ManageFamilyState.MEMBER_ACTIONS
                        },
                        onAddFamilyMember = onAddFamilyMember,
                        onDismiss = onDismiss
                    )
                }
                ManageFamilyState.MEMBER_ACTIONS -> {
                    val member = selectedFamilyMember ?: run {
                        currentScreenState = ManageFamilyState.LIST_OVERVIEW
                        return@AnimatedContent
                    }
                    MemberActionsScreen(
                        familyMember = member,
                        onManageChildSettingsClicked = {
                            // Initialize with dummy settings or fetch from data layer
                            currentChildSettings = ChildSettings(childId = member.id)
                            currentScreenState = ManageFamilyState.CHILD_SETTINGS
                        },
                        onEditRoleClicked = { currentScreenState = ManageFamilyState.SELECT_ROLE },
                        onRemoveFamilyMember = {
                            onRemoveFamilyMember(it)
                            onDismiss()
                        },
                        onBack = {
                            selectedFamilyMember = null
                            currentScreenState = ManageFamilyState.LIST_OVERVIEW
                        },
                        onDismiss = onDismiss
                    )
                }
                ManageFamilyState.CHILD_SETTINGS -> {
                    val member = selectedFamilyMember ?: run { // Child's data needed for display
                        currentScreenState = ManageFamilyState.LIST_OVERVIEW
                        return@AnimatedContent
                    }
                    val settings = currentChildSettings ?: run { // Actual settings to edit
                        currentScreenState = ManageFamilyState.MEMBER_ACTIONS
                        return@AnimatedContent
                    }
                    ChildSettingsScreen(
                        childDisplayName = member.displayName,
                        settings = settings,
                        onSave = { updatedSettings ->
                            onUpdateChildSettings(updatedSettings)
                            onDismiss() // Dismiss sheet after save
                        },
                        onCancel = {
                            currentChildSettings = null
                            currentScreenState = ManageFamilyState.MEMBER_ACTIONS // Go back to actions
                        }
                    )
                }
                ManageFamilyState.SELECT_ROLE -> {
                    val member = selectedFamilyMember ?: run {
                        currentScreenState = ManageFamilyState.LIST_OVERVIEW
                        return@AnimatedContent
                    }
                    SelectRoleScreen(
                        familyMember = member,
                        onRoleSelected = { newRole ->
                            onEditRole(member, newRole)
                            onDismiss()
                        },
                        onBack = { currentScreenState = ManageFamilyState.MEMBER_ACTIONS }
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyMemberListScreen(
    familyMembers: List<UserData>,
    onMemberClick: (UserData) -> Unit,
    onAddFamilyMember: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Manage Family",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            TextButton(onClick = onDismiss) {
                Text("Done", style = MaterialTheme.typography.labelLarge)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(familyMembers) { member ->
                FamilyMemberListItem(member = member) {
                    onMemberClick(member)
                }
            }
            item {
                AddFamilyMemberButton(onAddFamilyMember)
            }
        }
    }
}

@Composable
private fun FamilyMemberListItem(member: UserData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(member.profilePictureUrl)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = member.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = member.familyRole.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Member",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddFamilyMemberButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Add Family Member",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Add New Family Member",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MemberActionsScreen(
    familyMember: UserData,
    onManageChildSettingsClicked: () -> Unit, // New action
    onEditRoleClicked: () -> Unit,
    onRemoveFamilyMember: (UserData) -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to family list",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Manage ${familyMember.displayName}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))

        AsyncImage(
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .data(familyMember.profilePictureUrl)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            text = familyMember.displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        Text(
            text = "Current Role: ${familyMember.familyRole.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Only show "Manage Child Settings" if the member is a child
        if (familyMember.familyRole == FamilyRole.CHILD) {
            FilledTonalButton(
                onClick = onManageChildSettingsClicked,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Manage Child Settings", tint = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(Modifier.width(8.dp))
                Text("Manage Child Settings", style = MaterialTheme.typography.labelLarge)
            }
        }

        FilledTonalButton(
            onClick = onEditRoleClicked,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Role", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            Text("Change Family Role", style = MaterialTheme.typography.labelLarge)
        }

        Button(
            onClick = { onRemoveFamilyMember(familyMember) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Member")
            Spacer(Modifier.width(8.dp))
            Text("Remove Family Member", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onDismiss) {
            Text("Cancel", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ChildSettingsScreen(
    childDisplayName: String,
    settings: ChildSettings, // Pass in the mutable settings state
    onSave: (ChildSettings) -> Unit,
    onCancel: () -> Unit
) {
    // Hold mutable state for toggles
    var screenTimeEnabled by remember { mutableStateOf(settings.screenTimeLimitsEnabled) }
    var contentFilteringEnabled by remember { mutableStateOf(settings.contentFilteringEnabled) }
    var locationSharingEnabled by remember { mutableStateOf(settings.locationSharingEnabled) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to member actions",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Settings for $childDisplayName",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp) // Slight horizontal padding for content
        ) {
            Text(
                text = "Moderator Controls",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Example: Screen Time Limits
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { screenTimeEnabled = !screenTimeEnabled }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Screen Time Limits", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Control daily device usage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = screenTimeEnabled,
                    onCheckedChange = { screenTimeEnabled = it }
                )
            }
            Spacer(Modifier.height(4.dp))

            // Example: Content Filtering
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { contentFilteringEnabled = !contentFilteringEnabled }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Content Filtering", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Block inappropriate content", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = contentFilteringEnabled,
                    onCheckedChange = { contentFilteringEnabled = it }
                )
            }
            Spacer(Modifier.height(4.dp))

            // Example: Location Sharing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { locationSharingEnabled = !locationSharingEnabled }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Location Sharing", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Allow or deny location tracking", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = locationSharingEnabled,
                    onCheckedChange = { locationSharingEnabled = it }
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                onSave(
                    settings.copy(
                        screenTimeLimitsEnabled = screenTimeEnabled,
                        contentFilteringEnabled = contentFilteringEnabled,
                        locationSharingEnabled = locationSharingEnabled
                    )
                )
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Save Settings", style = MaterialTheme.typography.labelLarge)
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Cancel", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SelectRoleScreen(
    familyMember: UserData,
    onRoleSelected: (FamilyRole) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to member options",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Change Role for ${familyMember.displayName}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FamilyRole.entries.forEach { role ->
                if (role != FamilyRole.NONE) {
                    val isSelected = familyMember.familyRole == role
                    val buttonColors = if (isSelected) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { onRoleSelected(role) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = buttonColors,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (isSelected) 4.dp else 0.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.weight(1f))
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("Back", style = MaterialTheme.typography.labelLarge)
        }
    }
}

// --- Previews ---

@ThemePreview
@Composable
fun ManageFamilyBottomSheetContentPreview() {
    KorenTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Bottom
        ) {
            val familyMembers = remember {
                listOf(
                    UserData(id = "1", displayName = "Bojan Vilic", profilePictureUrl = "https://picsum.photos/id/1005/200/200", familyRole = FamilyRole.CHILD),
                    UserData(id = "2", displayName = "Bane Bane", profilePictureUrl = "https://picsum.photos/id/1011/200/200", familyRole = FamilyRole.PARENT),
                    UserData(id = "3", displayName = "Another Member", profilePictureUrl = "https://picsum.photos/id/1025/200/200", familyRole = FamilyRole.OTHER)
                )
            }

            ManageFamilyBottomSheetContent(
                familyMembers = familyMembers,
                onUpdateChildSettings = { _ -> },
                onEditRole = { _, _ -> },
                onRemoveFamilyMember = {},
                onDismiss = {},
                onAddFamilyMember = {},
                isLoading = false
            )
        }
    }
}

@ThemePreview
@Composable
fun ManageFamilyBottomSheetContentLoadingPreview() {
    KorenTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Bottom
        ) {
            val familyMembers = remember {
                listOf(
                    UserData(id = "1", displayName = "Bojan Vilic", profilePictureUrl = "https://picsum.photos/id/1005/200/200", familyRole = FamilyRole.CHILD),
                    UserData(id = "2", displayName = "Djoka Cvrki", profilePictureUrl = "https://picsum.photos/id/1011/200/200", familyRole = FamilyRole.PARENT)
                )
            }

            ManageFamilyBottomSheetContent(
                familyMembers = familyMembers,
                onUpdateChildSettings = { _ -> },
                onEditRole = { _, _ -> },
                onRemoveFamilyMember = {},
                onDismiss = {},
                onAddFamilyMember = {},
                isLoading = true
            )
        }
    }
}

@ThemePreview
@Composable
fun ManageChildSettingsScreenPreview() {
    KorenTheme {
        // This preview would typically be within the context of the bottom sheet
        // but can be displayed stand-alone for development.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val dummyChildSettings = remember { mutableStateOf(ChildSettings(childId = "dummy_child_id", screenTimeLimitsEnabled = true)) }
            ChildSettingsScreen(
                childDisplayName = "Bojan Vilic",
                settings = dummyChildSettings.value,
                onSave = { updatedSettings ->
                    dummyChildSettings.value = updatedSettings
                    // In a real app, you'd trigger a save action here
                    println("Saved Child Settings: $updatedSettings")
                },
                onCancel = {}
            )
        }
    }
}