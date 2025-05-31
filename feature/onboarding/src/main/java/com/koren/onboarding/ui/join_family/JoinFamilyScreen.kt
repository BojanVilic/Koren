package com.koren.onboarding.ui.join_family

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.getExpiryText
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.DateUtils.toRelativeTime
import com.koren.designsystem.components.DividerWithText
import com.koren.designsystem.components.InvitationCodeVisualTransformation
import com.koren.designsystem.components.StyledStringResource
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R
import kotlinx.serialization.Serializable

@Serializable
object JoinFamilyDestination

@Composable
fun JoinFamilyScreen(
    viewModel: JoinFamilyViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isBottomBarVisible = false,
            isTopBarVisible = false
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            else -> Unit
        }
    }

    JoinFamilyScreenContent(
        uiState = uiState
    )
}

@Composable
private fun JoinFamilyScreenContent(
    uiState: JoinFamilyUiState
) {
    when (uiState) {
        is JoinFamilyUiState.Loading -> CircularProgressIndicator()
        is JoinFamilyUiState.Shown -> JoinFamilyScreenShownContent(uiState = uiState)
        is JoinFamilyUiState.NoInvitations -> NoInvitationsContent(uiState = uiState)
    }
}

@Composable
private fun NoInvitationsContent(
    uiState: JoinFamilyUiState.NoInvitations
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No invitations found",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        val emailTextStyle = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        StyledStringResource(
            stringRes = R.string.no_invitations_text,
            formatArgs = listOf(
                uiState.userEmail to emailTextStyle
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun JoinFamilyScreenShownContent(
    uiState: JoinFamilyUiState.Shown
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .animateContentSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(uiState.receivedInvitations) { invitation ->
            ReceivedInvitationCard(
                invitation = invitation,
                invitationCodeText = uiState.invitationCodeText,
                acceptInvitation = { code -> uiState.eventSink(JoinFamilyUiEvent.AcceptInvitation(invitation, code)) },
                declineInvitation = { uiState.eventSink(JoinFamilyUiEvent.DeclineInvitation(invitation.id)) },
                onInvitationCodeChanged = { uiState.eventSink(JoinFamilyUiEvent.InvitationCodeChanged(it)) },
                invitationCodeError = uiState.invitationCodeError
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

@ThemePreview
@Composable
fun JoinFamilyScreenPreview() {
    KorenTheme {
        JoinFamilyScreenContent(
            uiState = JoinFamilyUiState.Shown(
                receivedInvitations = listOf(
                    Invitation(
                        id = "1",
                        familyName = "Smith Family",
                        senderName = "John Smith",
                        createdAt = System.currentTimeMillis(),
                    ),
                    Invitation(
                        id = "2",
                        familyName = "Doe Family",
                        senderName = "Jane Doe",
                        createdAt = System.currentTimeMillis() - 100000,
                    )
                ),
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
fun NoInvitationsContentPreview() {
    KorenTheme {
        NoInvitationsContent(
            uiState = JoinFamilyUiState.NoInvitations(userEmail = "johndoe@gmail.com")
        )
    }
}