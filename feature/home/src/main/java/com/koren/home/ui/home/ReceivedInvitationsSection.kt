package com.koren.home.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.getExpiryText
import com.koren.common.util.DateUtils.toRelativeTime

internal fun LazyListScope.receivedInvitationsSection(
    state: HomeUiState.Shown
) {
    item {
        if (state.receivedInvitations.isNotEmpty()) {
            Column {
                state.receivedInvitations.forEach { invitation ->
                    ReceivedInvitationCard(
                        invitation = invitation,
                        invitationCodeText = state.invitationCodeText,
                        acceptInvitation = { code -> state.eventSink(HomeEvent.AcceptInvitation(invitation, code)) },
                        declineInvitation = { state.eventSink(HomeEvent.DeclineInvitation(invitation.id)) },
                        onInvitationCodeChanged = { state.eventSink(HomeEvent.InvitationCodeChanged(it)) },
                        invitationCodeError = state.invitationCodeError
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceivedInvitationCard(
    invitation: Invitation,
    invitationCodeText: String,
    acceptInvitation: (String) -> Unit,
    declineInvitation: () -> Unit,
    onInvitationCodeChanged: (String) -> Unit,
    invitationCodeError: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            text = invitation.status.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                text = "You have a new invitation from ${invitation.senderName} to join ${invitation.familyName}.",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            DividerWithText(
                modifier = Modifier.padding(vertical = 16.dp),
                text = invitation.createdAt.toRelativeTime()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = invitationCodeText,
                    onValueChange = {
                        if (it.length <= 6) onInvitationCodeChanged(it.uppercase())
                    },
                    label = { Text("Invitation code") },
                    supportingText = { Text(invitationCodeError) },
                    isError = invitationCodeError.isNotEmpty(),
                    visualTransformation = InvitationCodeVisualTransformation()
                )

                Spacer(modifier = Modifier.width(32.dp))

                FilledIconButton(
                    onClick = { acceptInvitation(invitationCodeText) },
                    enabled = invitationCodeText.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Accept invitation"
                    )
                }

                FilledIconButton(
                    onClick = { declineInvitation() },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline invitation"
                    )
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = invitation.getExpiryText(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}