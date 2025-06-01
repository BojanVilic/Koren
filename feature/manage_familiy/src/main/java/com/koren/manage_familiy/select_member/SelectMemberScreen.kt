package com.koren.manage_familiy.select_member

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.koren.common.models.family.FamilyRole
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
data object SelectMemberDestination

@Composable
fun SelectMemberScreen(
    viewModel: SelectMemberViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit,
    onNavigateToEditMember: (String) -> Unit,
    onNavigateToAddNewMember: () -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = true,
            title = "Manage Family",
            isBottomBarVisible = false
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            is SelectMemberUiSideEffect.NavigateToEditMember -> onNavigateToEditMember(sideEffect.memberId)
            is SelectMemberUiSideEffect.NavigateToAddNewMember -> onNavigateToAddNewMember()
        }
    }

    SelectMemberScreenContent(uiState)
}

@Composable
private fun SelectMemberScreenContent(uiState: SelectMemberUiState) {
    when (uiState) {
        is SelectMemberUiState.Loading -> LoadingContent()
        is SelectMemberUiState.Shown -> SelectMemberScreenShownContent(uiState)
    }
}

@Composable
private fun SelectMemberScreenShownContent(uiState: SelectMemberUiState.Shown) {
    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.members) { member ->
                FamilyMemberListItem(
                    member = member,
                    onClick = {
                        uiState.eventSink(SelectMemberUiEvent.MemberSelected(member))
                    }
                )
            }
            item {
                AddFamilyMemberButton(
                    onClick = {
                        uiState.eventSink(SelectMemberUiEvent.AddMemberClicked)
                    }
                )
            }
        }
    }
}

@Composable
private fun FamilyMemberListItem(
    member: UserData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                    .clip(MaterialTheme.shapes.large),
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
private fun AddFamilyMemberButton(
    onClick: () -> Unit
) {
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

@ThemePreview
@Composable
private fun SelectMemberScreenPreview() {
    KorenTheme {
        SelectMemberScreenContent(
            SelectMemberUiState.Shown(
                members = listOf(
                    UserData(
                        id = "1",
                        displayName = "John Doe",
                        profilePictureUrl = "https://example.com/john.jpg",
                        familyRole = FamilyRole.PARENT
                    ),
                    UserData(
                        id = "2",
                        displayName = "Jane Smith",
                        profilePictureUrl = "https://example.com/jane.jpg",
                        familyRole = FamilyRole.CHILD
                    )
                ),
                eventSink = {}
            )
        )
    }
}