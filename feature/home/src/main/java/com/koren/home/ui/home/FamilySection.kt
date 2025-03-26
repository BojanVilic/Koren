package com.koren.home.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.common.models.user.UserData

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
                LazyRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(state.familyMembers) { member ->
                        FamilyMember(
                            member = member,
                            familyMemberClicked = { state.eventSink(HomeEvent.FamilyMemberClicked(member)) }
                        )
                    }
                    item {
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
}

@Composable
private fun FamilyMember(
    member: UserData,
    familyMemberClicked: () -> Unit
) {
    Card(
        onClick = { familyMemberClicked() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(member.profilePictureUrl)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = member.displayName,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}