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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.koren.common.models.Invitation
import com.koren.common.models.InvitationStatus
import com.koren.common.models.getExpiryText
import com.koren.common.models.toHumanReadableDateTime
import com.koren.common.models.toRelativeTime
import com.koren.common.util.Destination
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
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
    inviteFamilyMember: () -> Unit,
    removeFamilyMember: () -> Unit = {},
    scheduleEvent: () -> Unit = {},
    reminder: () -> Unit = {},
    chat: () -> Unit = {}
) {

    val actions = listOf(
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Email),
            text = "Chat",
            onClick = chat
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.DateRange),
            text = "Schedule",
            onClick = scheduleEvent
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Notifications),
            text = "Reminder",
            onClick = reminder
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Add),
            text = "Invite",
            onClick = inviteFamilyMember
        ),
        ActionItem(
            icon = IconResource.Drawable(R.drawable.remove_person),
            text = "Remove",
            onClick = removeFamilyMember
        )
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        if (state.receivedInvitations.isNotEmpty()) {
            LazyColumn {
                items(state.receivedInvitations) { invitation ->
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

        if (state.sentInvitations.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.sentInvitations) { invitation ->
                    SentInvitationCard(invitation = invitation)
                }
            }
        }

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "Actions"
        )
        Card {
            LazyRow(
                modifier = Modifier.padding(8.dp)
            ) {
                items(actions) { actionItem ->
                    ActionButton(actionItem)
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    actionItem: ActionItem
) {
    OutlinedButton(
        onClick = actionItem.onClick,
        border = null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            actionItem.IconComposable()
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = actionItem.text
            )
        }
    }
}

@Composable
private fun SentInvitationCard(
    invitation: Invitation
) {
    Card {
        Row {
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(12.dp),
                text = invitation.getExpiryText(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                text = invitation.recipientEmail,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.Underline
            )
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
            .padding(vertical = 8.dp)
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
                receivedInvitations = listOf(
                    Invitation(
                        id = "1",
                        senderId = "sender1",
                        status = InvitationStatus.PENDING,
                        expirationDate = 1630000000000,
                        recipientEmail = ""
                    )
                ),
                sentInvitations = listOf(
                    Invitation(
                        status = InvitationStatus.ACCEPTED,
                        recipientEmail = "johndoe@email.com"
                    ),
                    Invitation(
                        status = InvitationStatus.PENDING,
                        createdAt = 1735958071,
                        expirationDate = 1735958177,
                        recipientEmail = "johndoe@email.com"
                    ),
                    Invitation(
                        status = InvitationStatus.DECLINED,
                        recipientEmail = "johndoe@email.com"
                    ),
                    Invitation(
                        status = InvitationStatus.EXPIRED,
                        recipientEmail = "johndoe@email.com"
                    )
                ),
                eventSink = {}
            ),
            inviteFamilyMember = {}
        )
    }
}