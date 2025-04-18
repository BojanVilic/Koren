package com.koren.onboarding.ui.create_family

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koren.common.models.invitation.InvitationResult
import com.koren.designsystem.components.invitation.EmailExpandedContent
import com.koren.designsystem.components.invitation.EmailInviteSentContent
import com.koren.designsystem.components.invitation.InvitationChoiceCard
import com.koren.designsystem.components.invitation.LoadingSpinner
import com.koren.designsystem.components.invitation.QRExpandedContent
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.QrCode
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R

@Composable
internal fun InviteFamilyMembersStep(
    uiState: CreateFamilyUiState.Step
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.invite_members_title),
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            text = stringResource(R.string.invite_members_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        InvitationChoiceCard(
            title = stringResource(id = com.koren.designsystem.R.string.share_qr_code),
            subtitle = stringResource(id = com.koren.designsystem.R.string.scan_qr_code_description),
            icon = KorenIcons.QrCode,
            isExpanded = uiState.isCreateQRInvitationExpanded,
            onCardClicked = {
                if (uiState.isCreateQRInvitationExpanded)
                    uiState.eventSink(CreateFamilyEvent.CollapseCreateQRInvitation)
                else
                    uiState.eventSink(CreateFamilyEvent.CreateQRInvitation)
            },
            expandedContent = {
                if (uiState.qrInvitationLoading) {
                    LoadingSpinner()
                } else {
                    uiState.qrInvitation?.let { qrInvitation ->
                        QRExpandedContent(
                            invitationLink = qrInvitation.invitationLink,
                            familyName = uiState.familyName
                        )
                    }
                }
            }
        )

        InvitationChoiceCard(
            title = stringResource(id = com.koren.designsystem.R.string.invite_via_email),
            subtitle = stringResource(id = com.koren.designsystem.R.string.invite_via_email_description),
            icon = Icons.Default.Email,
            isExpanded = uiState.isEmailInviteExpanded,
            onCardClicked = {
                uiState.eventSink(CreateFamilyEvent.EmailInviteClick)
            },
            expandedContent = {
                when {
                    uiState.emailInvitationLoading -> LoadingSpinner()
                    uiState.emailInvitation == null -> EmailExpandedContent(
                        emailInviteText = uiState.emailInviteText,
                        onEmailInviteTextChange = {
                            uiState.eventSink(CreateFamilyEvent.EmailInviteTextChange(it))
                        },
                        onInviteViaEmailClick = {
                            uiState.eventSink(CreateFamilyEvent.InviteViaEmailClick)
                        }
                    )
                    else -> EmailInviteSentContent(
                        emailInvitation = uiState.emailInvitation,
                        emailInviteText = uiState.emailInviteText
                    )
                }
            }
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = stringResource(R.string.invite_members_later_info),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp),
            text = stringResource(id = com.koren.designsystem.R.string.expiration_info_label),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@ThemePreview
@Composable
private fun InviteFamilyMembersStepPreview() {
    KorenTheme {
        InviteFamilyMembersStep(
            uiState = CreateFamilyUiState.Step(
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