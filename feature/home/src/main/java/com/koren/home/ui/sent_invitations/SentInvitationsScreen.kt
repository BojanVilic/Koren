package com.koren.home.ui.sent_invitations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.invitation.getExpiryText
import com.koren.common.models.invitation.isQRInvitation
import com.koren.common.util.DateUtils.toRelativeTime
import com.koren.designsystem.components.BrokenBranchErrorScreen
import com.koren.designsystem.components.EmptyContent
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.components.QRCodeImage
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import kotlinx.serialization.Serializable

@Serializable
object SentInvitationsDestination

@Composable
fun SentInvitationsScreen(
    sentInvitationViewModel: SentInvitationViewModel = hiltViewModel()
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            title = "Sent Invitations",
            isTopBarVisible = true,
            isBottomBarVisible = true
        )
    )

    val uiState by sentInvitationViewModel.uiState.collectAsStateWithLifecycle()

    SentInvitationsContent(
        uiState = uiState
    )
}

@Composable
private fun SentInvitationsContent(
    uiState: SentInvitationUiState
) {

    when (uiState) {
        is SentInvitationUiState.Loading -> LoadingContent()
        is SentInvitationUiState.Error -> BrokenBranchErrorScreen(uiState.message)
        is SentInvitationUiState.Shown -> ShownContent(uiState)
        is SentInvitationUiState.Empty -> EmptyContent()
    }
}

@Composable
private fun ShownContent(
    uiState: SentInvitationUiState.Shown
) {
    LazyColumn(
        modifier = Modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(uiState.sentInvitations) { invitation ->
            SentInvitationItem(
                uiInvitation = invitation,
                onClick = {
                    uiState.eventSink(SentInvitationEvent.ExpandQRCode(it))
                }
            )
        }
    }
}

@Composable
private fun SentInvitationItem(
    uiInvitation: UiSentInvitation,
    onClick: (String) -> Unit
) {
    val (_, invitation) = uiInvitation

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onClick(invitation.id)
                }
            ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (invitation.isQRInvitation()) {
                QRInvitation(
                    uiInvitation = uiInvitation,
                    onClick = onClick
                )
            } else {
                EmailInvitation(
                    invitation = invitation
                )
            }
        }
    }
}

@Composable
private fun EmailInvitation(
    invitation: Invitation
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Email,
                contentDescription = "Email invitation"
            )
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
            Spacer(modifier = Modifier.weight(1f))
            Text(text = invitation.getExpiryText())
        }

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = "Recipient: ${invitation.recipientEmail}",
            style = MaterialTheme.typography.bodyLarge
        )

        val invitationCode = invitation.invitationCode
        val firstPart = invitationCode.substring(startIndex = 0, endIndex = invitationCode.length / 2)
        val secondPart = invitationCode.substring(startIndex = invitationCode.length / 2)

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = buildAnnotatedString {
                append("Invitation code: ")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(firstPart)
                    append(" ")
                    append(secondPart)
                }
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = "Created at: ${invitation.createdAt.toRelativeTime()}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun QRInvitation(
    uiInvitation: UiSentInvitation,
    onClick: (String) -> Unit
) {

    val (expanded, invitation) = uiInvitation

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.qr_code),
                contentDescription = "QR Code invitation"
            )
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
            Spacer(modifier = Modifier.weight(1f))
            Text(text = invitation.getExpiryText())

        }

        if (invitation.status != InvitationStatus.ACCEPTED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onClick(invitation.id) }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
        }


        if (expanded) {
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.size(16.dp))
            QRCodeImage(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .size(200.dp),
                data = invitation.invitationLink
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = "Created at: ${invitation.createdAt.toRelativeTime()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@ThemePreview
@Composable
private fun ShownContentPreview() {
    ShownContent(
        uiState = SentInvitationUiState.Shown(
            sentInvitations = listOf(
                UiSentInvitation(
                    invitation = Invitation(
                        createdAt = 1630000000000,
                        expirationDate = 1630000000000,
                        status = InvitationStatus.PENDING
                    ),
                    expanded = false
                ),
                UiSentInvitation(
                    invitation = Invitation(
                        createdAt = 1630000000000,
                        expirationDate = 1630000000000,
                        status = InvitationStatus.PENDING
                    ),
                    expanded = true
                ),
            ),
            eventSink = {}
        )
    )
}