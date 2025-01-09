package com.koren.home.ui.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.Invitation
import com.koren.common.models.getExpiryText
import com.koren.common.models.toRelativeTime
import com.koren.common.util.Destination
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.ui.DividerWithText
import kotlinx.serialization.Serializable

@Serializable
data class QRAcceptInvitationDestination(
    val invitationId: String,
    val familyId: String,
    val invitationCode: String
) : Destination

@Composable
fun QRAcceptInvitationScreen(
    invitationId: String,
    familyId: String,
    invitationCode: String,
    qrInvitationViewModel: QRInvitationViewModel = hiltViewModel(),
    onNavigateToHome: suspend (errorMessage: String) -> Unit
) {

    val uiState by qrInvitationViewModel.state.collectAsStateWithLifecycle()

    LocalScaffoldStateProvider.current.setScaffoldState(state = ScaffoldState(isTopBarVisible = false))

    LaunchedEffect(Unit) {
        qrInvitationViewModel.getQRInvitation(
            invId = invitationId,
            familyId = familyId,
            invCode = invitationCode
        )
    }

    QRAcceptInvitationContent(
        uiState = uiState,
        invitationCode = invitationCode,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun QRAcceptInvitationContent(
    uiState: QRInvitationUiState,
    invitationCode: String,
    onNavigateToHome: suspend (errorMessage: String) -> Unit
) {
    when (uiState) {
        is QRInvitationUiState.Loading -> LoadingContent()
        is QRInvitationUiState.Shown -> ShownContent(
            uiState = uiState,
            acceptInvitation = { uiState.eventSink(QRInvitationUiEvent.AcceptInvitation(invitationCode)) },
            declineInvitation = { uiState.eventSink(QRInvitationUiEvent.DeclineInvitation) }
        )
        is QRInvitationUiState.NavigateToHome -> NavigateToHomeWithErrorMessageContent(
            uiState = uiState,
            onNavigateToHome = onNavigateToHome
        )
    }
}

@Composable
private fun NavigateToHomeWithErrorMessageContent(
    uiState: QRInvitationUiState.NavigateToHome,
    onNavigateToHome: suspend (errorMessage: String) -> Unit
) {
    LaunchedEffect(Unit) { onNavigateToHome(uiState.errorMessage) }
}

@Composable
private fun ShownContent(
    uiState: QRInvitationUiState.Shown,
    acceptInvitation: () -> Unit,
    declineInvitation: () -> Unit,
) {
    val invitation = uiState.invitation

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    FilledIconButton(
                        onClick = { acceptInvitation() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Accept invitation"
                        )
                    }

                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))

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
}

@ThemePreview
@Composable
fun QRAcceptInvitationScreenPreview() {
    QRAcceptInvitationContent(
        uiState = QRInvitationUiState.Shown(
            invitation = Invitation(),
            eventSink = {}
        ),
        invitationCode = "123456",
        onNavigateToHome = {}
    )
}