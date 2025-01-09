package com.koren.invitation.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.koren.common.models.InvitationResult
import com.koren.common.util.Destination
import com.koren.designsystem.components.BrokenBranchErrorScreen
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.components.StyledStringResource
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.invitation.R
import kotlinx.serialization.Serializable
import qrcode.QRCode
import qrcode.color.Colors

@Serializable
object InvitationDestination : Destination

@Composable
fun InvitationScreen(
    invitationViewModel: InvitationViewModel = hiltViewModel(),
) {
    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            title = stringResource(R.string.invite_screen_title),
            isBottomBarVisible = false
        )
    )

    val invitationUiState by invitationViewModel.state.collectAsStateWithLifecycle()

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
            title = stringResource(id = R.string.share_qr_code),
            subtitle = stringResource(id = R.string.scan_qr_code_description),
            icon = R.drawable.qr_code,
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
            title = stringResource(id = R.string.invite_via_email),
            subtitle = stringResource(id = R.string.invite_via_email_description),
            icon = R.drawable.ic_email,
            isExpanded = invitationUiState.isEmailInviteExpanded,
            onCardClicked = {
                invitationUiState.eventSink(InvitationEvent.EmailInviteClick)
            },
            expandedContent = {
                when {
                    invitationUiState.emailInvitationLoading -> LoadingSpinner()
                    invitationUiState.emailInvitation == null -> EmailExpandedContent(invitationUiState = invitationUiState)
                    else -> EmailInviteSentContent(invitationUiState = invitationUiState)
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(id = R.string.expiration_info_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun LoadingSpinner() {
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
    invitationUiState: InvitationUiState.Shown
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
                    text = invitationUiState.emailInviteText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (invitationUiState.emailInvitation != null) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(R.string.invitation_code_instruction),
                    style = MaterialTheme.typography.bodyMedium
                )

                val invitationCode = invitationUiState.emailInvitation.invitationCode
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
private fun EmailExpandedContent(
    invitationUiState: InvitationUiState.Shown
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
                    value = invitationUiState.emailInviteText,
                    onValueChange = { invitationUiState.eventSink(InvitationEvent.EmailInviteTextChange(it)) },
                    label = { Text(text = stringResource(R.string.label_email_address)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                )

                Button(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { invitationUiState.eventSink(InvitationEvent.InviteViaEmailClick) },
                    enabled = invitationUiState.isEmailInviteButtonEnabled
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
private fun QRExpandedContent(
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
private fun InvitationChoiceCard(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
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
                painter = painterResource(id = icon),
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

@Composable
fun QRCodeImage(
    modifier: Modifier = Modifier,
    data: String,
    color: Int = Colors.BLACK,
    backgroundColor: Int = Colors.WHITE
) {
    val qrCodeBitmap = remember(data) {
        generateQrCodeBitmap(data, color, backgroundColor)
    }

    qrCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier
        )
    }
}

private fun generateQrCodeBitmap(data: String, color: Int, backgroundColor: Int): Bitmap? {
    return try {
        val qrCode = QRCode.ofSquares()
            .withColor(color)
            .withBackgroundColor(backgroundColor)
            .build(data)
        val qrCodeImage = qrCode.render().getBytes()
        BitmapFactory.decodeByteArray(qrCodeImage, 0, qrCodeImage.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
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