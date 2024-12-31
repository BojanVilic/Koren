package com.koren.invitation.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.util.Destination
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
        is InvitationUiState.Error -> Text("There was an error creating the invitation.")
        is InvitationUiState.Idle -> IdleState(invitationUiState)
        is InvitationUiState.InvitationCreated -> InvitationCreated(invitationUiState)
        is InvitationUiState.Loading -> CircularProgressIndicator()
    }
}

@Composable
private fun IdleState(
    invitationUiState: InvitationUiState.Idle
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.7f),
                    onClick = { invitationUiState.eventSink(InvitationEvent.CreateInvitation) }
                ) {
                    Icon(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        painter = painterResource(id = R.drawable.qr_code),
                        contentDescription = stringResource(id = R.string.share_qr_code)
                    )
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = stringResource(id = R.string.share_qr_code),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.7f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.invite_via_email))
                    Icon(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(id = R.string.invite_via_email)
                    )
                }
            }
        }

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
private fun InvitationCreated(
    invitationUiState: InvitationUiState.InvitationCreated
) {
    val message = """
        Join my family on Koren!
        Tap this link (or copy and paste it into your browser if tapping doesn't work):
        ${invitationUiState.invitationResult.invitationLink}
    """.trimIndent()

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_SUBJECT, "Join my family on Koren")
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Invitation")
    val context = LocalContext.current

    startActivity(context, shareIntent, null)

    Column {
        Text(text = "Invitation created!")
        Text(
            text = "Invitation code: ${invitationUiState.invitationResult.invitationCode}",
            style = MaterialTheme.typography.displayLarge
        )

        QRCodeImage(
            modifier = Modifier.size(200.dp),
            data = invitationUiState.invitationResult.invitationLink
        )
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

@Preview(showBackground = true)
@Composable
fun QRCodePreview() {
    QRCodeImage(
        modifier = Modifier.size(200.dp),
        data = "koren://join?familyId%3D8bcfed49-f9d2-42a5-95e1-aa20fb4cbb5e%26invCode%3D2F57D9"
    )
}

@ThemePreview
@Composable
fun InvitationPreview() {
    KorenTheme {
        InvitationContent(
            invitationUiState = InvitationUiState.Idle(
                eventSink = {}
            )
        )
    }
}