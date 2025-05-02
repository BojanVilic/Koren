package com.koren.home.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.common.models.user.UserData
import com.koren.common.util.formatDistanceToText
import com.koren.designsystem.icon.CallHome
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.common.models.family.FamilyMemberUserData

internal fun LazyListScope.familySection(
    state: HomeUiState.Shown
) {
    item {
        if (state.familyMembers.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.familyMembers.forEach { member ->
                        FamilyMember(
                            member = member,
                            familyMemberClicked = { state.eventSink(HomeEvent.FamilyMemberClicked(member)) }
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { state.eventSink(HomeEvent.NavigateToInviteFamilyMember) }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(64.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add family member"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyMember(
    member: FamilyMemberUserData,
    familyMemberClicked: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val cardBackgroundColor = if (member.goingHome) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
    val nameColor = if (member.goingHome) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface
    val distanceColor = if (member.goingHome) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxHeight(),
        onClick = {
            familyMemberClicked()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
        },
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(member.userData.profilePictureUrl)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                if (member.goingHome) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Icon(
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Top),
                        imageVector = KorenIcons.CallHome,
                        contentDescription = "Going Home",
                        tint = nameColor
                    )
                }
            }

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = member.userData.displayName,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = nameColor,
            )

            if (member.goingHome) {
                if (member.distance > 0) {
                    Text(
                        text = member.distance.formatDistanceToText(),
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = distanceColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Going home",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = distanceColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@ThemePreview
@Composable
private fun FamilySectionPreview() {
    val familyMembers = remember {
        listOf(
            FamilyMemberUserData(
                userData = UserData(
                    id = "1",
                    displayName = "John Doe",
                    profilePictureUrl = "https://example.com/john.jpg"
                ),
                distance = 2000,
                goingHome = true
            ),
            FamilyMemberUserData(
                userData = UserData(
                    id = "2",
                    displayName = "Jane Smith",
                    profilePictureUrl = "https://example.com/jane.jpg"
                ),
                distance = 5050,
                goingHome = false
            )
        )
    }

    KorenTheme {
        Surface {
            LazyColumn {
                familySection(
                    state = HomeUiState.Shown(
                        familyMembers = familyMembers,
                        eventSink = {}
                    )
                )
            }
        }
    }
}