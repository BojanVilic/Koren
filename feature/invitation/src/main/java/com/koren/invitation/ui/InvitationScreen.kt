package com.koren.invitation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.invitation.InvitationResult
import com.koren.designsystem.components.BrokenBranchErrorScreen
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.components.invitation.EmailExpandedContent
import com.koren.designsystem.components.invitation.EmailInviteSentContent
import com.koren.designsystem.components.invitation.InvitationChoiceCard
import com.koren.designsystem.components.invitation.LoadingSpinner
import com.koren.designsystem.components.invitation.QRExpandedContent
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.QrCode
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.invitation.R
import kotlinx.serialization.Serializable

@Serializable
object InvitationDestination

@Composable
fun InvitationScreen(
    invitationViewModel: InvitationViewModel = hiltViewModel(),
) {
    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            title = stringResource(com.koren.designsystem.R.string.invite_screen_title),
            isBottomBarVisible = false
        )
    )

    val invitationUiState by invitationViewModel.uiState.collectAsStateWithLifecycle()

    InvitationContent(
        invitationUiState = invitationUiState
    )
}

@Composable
private fun InvitationContent(
    invitationUiState: InvitationUiState
) {

    when (invitationUiState) {
        is InvitationUiState.Error -> BrokenBranchErrorScreen(errorMessage = invitationUiState.errorMessage)
        is InvitationUiState.Shown -> ShownContent(invitationUiState = invitationUiState)
    }
}

@Composable
private fun ShownContent(
    invitationUiState: InvitationUiState.Shown
) {
    if (invitationUiState.errorMessage.isNotEmpty()) {
        SimpleSnackbar(message = invitationUiState.errorMessage)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InvitationChoiceCard(
            title = stringResource(id = com.koren.designsystem.R.string.share_qr_code),
            subtitle = stringResource(id = com.koren.designsystem.R.string.scan_qr_code_description),
            icon = KorenIcons.QrCode,
            isExpanded = invitationUiState.isCreateQRInvitationExpanded,
            onCardClicked = {
                if (invitationUiState.isCreateQRInvitationExpanded)
                    invitationUiState.eventSink(InvitationEvent.CollapseCreateQRInvitation)
                else
                    invitationUiState.eventSink(InvitationEvent.CreateQRInvitation)
            },
            expandedContent = {
                if (invitationUiState.qrInvitationLoading) {
                    LoadingSpinner()
                } else {
                    invitationUiState.qrInvitation?.let { qrInvitation ->
                        QRExpandedContent(
                            invitationLink = qrInvitation.invitationLink,
                            familyName = invitationUiState.familyName
                        )
                    }
                }
            }
        )

        InvitationChoiceCard(
            title = stringResource(id = com.koren.designsystem.R.string.invite_via_email),
            subtitle = stringResource(id = com.koren.designsystem.R.string.invite_via_email_description),
            icon = Icons.Default.Email,
            isExpanded = invitationUiState.isEmailInviteExpanded,
            onCardClicked = {
                invitationUiState.eventSink(InvitationEvent.EmailInviteClick)
            },
            expandedContent = {
                when {
                    invitationUiState.emailInvitationLoading -> LoadingSpinner()
                    invitationUiState.emailInvitation == null -> EmailExpandedContent(
                        emailInviteText = invitationUiState.emailInviteText,
                        onEmailInviteTextChange = {
                            invitationUiState.eventSink(InvitationEvent.EmailInviteTextChange(it))
                        },
                        onInviteViaEmailClick = {
                            invitationUiState.eventSink(InvitationEvent.InviteViaEmailClick)
                        }
                    )
                    else -> EmailInviteSentContent(
                        emailInvitation = invitationUiState.emailInvitation,
                        emailInviteText = invitationUiState.emailInviteText
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(id = com.koren.designsystem.R.string.expiration_info_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@ThemePreview
@Composable
fun InvitationPreview() {
    KorenTheme {
        InvitationContent(
            invitationUiState = InvitationUiState.Shown(
                familyName = "Family Name",
                isCreateQRInvitationExpanded = true,
                isEmailInviteExpanded = true,
                emailInviteText = "johndoe@gmail.com",
                qrInvitation = InvitationResult(
                    invitationLink = "koren://join?familyId%3D8bcfed49-f9d2-42a5-95e1-aa20fb4cbb5e%26invCode%3D2F57D9"
                ),
                emailInvitation = InvitationResult(
                    invitationCode = "2F57D9"
                ),
                eventSink = {}
            )
        )
    }
}