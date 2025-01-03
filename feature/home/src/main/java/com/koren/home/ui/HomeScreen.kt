package com.koren.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationStatus
import com.koren.common.models.toHumanReadableDateTime
import com.koren.common.models.toRelativeTime
import com.koren.common.util.Destination
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination : Destination

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    inviteFamilyMember: () -> Unit
) {

    val scaffoldStateProvider = LocalScaffoldStateProvider.current
    DisposableEffectWithLifecycle(onStart = { scaffoldStateProvider.setScaffoldState(ScaffoldState(isTopBarVisible = false)) })

    val state by homeViewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        inviteFamilyMember = inviteFamilyMember
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    inviteFamilyMember: () -> Unit
) {
    when (state) {
        is HomeUiState.Loading -> Text("Loading...")
        is HomeUiState.Shown -> ShownContent(state = state, inviteFamilyMember = inviteFamilyMember)
    }
}

@Composable
private fun ShownContent(
    state: HomeUiState.Shown,
    inviteFamilyMember: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        if (state.pendingInvitations.isNotEmpty()) {
            LazyColumn {
                items(state.pendingInvitations) { invitation ->
                    InvitationCard(
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


        Button(
            onClick = { inviteFamilyMember() }
        ) {
            Text("Invite a family member")
        }

    }
}

@Composable
private fun InvitationCard(
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
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
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
                    .padding(bottom = 16.dp),
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
                    onClick = { declineInvitation() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline invitation"
                    )
                }
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Expires at ${invitation.expirationDate.toHumanReadableDateTime()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

class InvitationCodeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 6) text.text.substring(0, 6) else text.text
        val upperCase = trimmed.uppercase()
        val spaced = buildString {
            upperCase.forEachIndexed { index, char ->
                append(char)
                if ((index + 1) % 3 == 0 && index < upperCase.length - 1) {
                    append(" ")
                }
            }
        }

        return TransformedText(AnnotatedString(spaced), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                val spacedOffset = offset + (offset - 1) / 3
                return minOf(spacedOffset, spaced.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return offset
                val originalOffset = offset - (offset - 1) / 4 // Divide by 4 because of added space
                return minOf(originalOffset, upperCase.length)
            }
        })
    }
}

@Composable
fun DividerWithText(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.weight(0.6f),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@ThemePreview
@Composable
fun HomePreview() {
    KorenTheme {
        HomeContent(
            state = HomeUiState.Shown(
                pendingInvitations = listOf(
                    Invitation(
                        id = "1",
                        senderId = "sender1",
                        status = InvitationStatus.PENDING,
                        expirationDate = 1630000000000,
                        recipientEmail = ""
                    ),
                    Invitation(
                        id = "2",
                        senderId = "sender2",
                        status = InvitationStatus.DECLINED,
                        expirationDate = 1630000000000,
                        recipientEmail = ""
                    )
                ),
                eventSink = {}
            ),
            inviteFamilyMember = {}
        )
    }
}