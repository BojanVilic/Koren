package com.koren.designsystem.components.invitation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koren.common.models.invitation.InvitationResult
import com.koren.designsystem.R
import com.koren.designsystem.components.QRCodeImage
import com.koren.designsystem.components.StyledStringResource
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.QrCode
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
fun LoadingSpinner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun EmailInviteSentContent(
    emailInviteText: String,
    emailInvitation: InvitationResult? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.invitation_sent_to),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    modifier = Modifier
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = emailInviteText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (emailInvitation != null) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(R.string.invitation_code_instruction),
                    style = MaterialTheme.typography.bodyMedium
                )

                val invitationCode = emailInvitation.invitationCode
                val firstPart = invitationCode.substring(startIndex = 0, endIndex = invitationCode.length / 2)
                val secondPart = invitationCode.substring(startIndex = invitationCode.length / 2)

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    text = "$firstPart $secondPart",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmailExpandedContent(
    emailInviteText: String,
    onEmailInviteTextChange: (String) -> Unit,
    onInviteViaEmailClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    value = emailInviteText,
                    onValueChange = { onEmailInviteTextChange(it) },
                    label = { Text(text = stringResource(R.string.label_email_address)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                )

                Button(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onInviteViaEmailClick() },
                    enabled = emailInviteText.isNotBlank()
                ) {
                    Text(text = stringResource(R.string.label_invite))
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = stringResource(id = R.string.invite_via_email_description),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun QRExpandedContent(
    invitationLink: String,
    familyName: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QRCodeImage(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(200.dp),
                data = invitationLink
            )
            StyledStringResource(
                modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp),
                stringRes = R.string.scan_qr_code_subtitle,
                formatArgs = listOf(familyName to SpanStyle(color = MaterialTheme.colorScheme.primary)),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InvitationChoiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onCardClicked: () -> Unit,
    expandedContent : @Composable () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "Chevron rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onCardClicked()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(64.dp)
                    .padding(horizontal = 4.dp),
                imageVector = icon,
                contentDescription = stringResource(id = R.string.share_qr_code)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotationAngle),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }

        AnimatedVisibility(isExpanded) {
            expandedContent()
        }
    }
}

@ThemePreview
@Composable
private fun InvitationChoiceCardPreview() {
    KorenTheme {
        InvitationChoiceCard(
            title = "Share QR Code",
            subtitle = "Scan QR code to join family",
            icon = KorenIcons.QrCode,
            isExpanded = true,
            onCardClicked = {},
            expandedContent = {
                QRExpandedContent(
                    invitationLink = "https://example.com/invitation",
                    familyName = "Smith Family"
                )
            }
        )
    }
}