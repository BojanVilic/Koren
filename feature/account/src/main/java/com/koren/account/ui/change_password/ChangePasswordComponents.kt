package com.koren.account.ui.change_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.R
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit
) {

    DisposableEffectWithLifecycle(
        onCreate = { viewModel.init() },
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { sideEffect ->
        when (sideEffect) {
            is ChangePasswordSideEffect.Close -> onDismissRequest()
        }
    }

    ChangePasswordContent(uiState = uiState)
}

@Composable
private fun ChangePasswordContent(
    uiState: ChangePasswordUiState
) {
    when (uiState) {
        is ChangePasswordUiState.Loading -> LoadingContent()
        is ChangePasswordUiState.Shown -> ShownContent(uiState = uiState)
        is ChangePasswordUiState.EmailSent -> EmailSentContent(uiState = uiState)
    }
}

@Composable
private fun ShownContent(
    uiState: ChangePasswordUiState.Shown
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(16.dp)
    ) {
        Text(
            text = "Oops! Forgot Your Password?",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Don't worry, it happens! Enter your email below and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            value = uiState.email,
            onValueChange = { uiState.eventSink(ChangePasswordUiEvent.EmailChanged(it)) },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            enabled = !uiState.emailSent
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            onClick = { uiState.eventSink(ChangePasswordUiEvent.SendResetPasswordEmail) }
        ) {
            Text("Send Reset Password Email")
        }
    }
}

@Composable
private fun EmailSentContent(
    uiState: ChangePasswordUiState.EmailSent
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.email_sent))
    val preloaderProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(16.dp)
    ) {
        Text(
            text = "Password Reset Email Sent!",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 32.dp)
                .align(Alignment.CenterHorizontally),
            value = uiState.email,
            onValueChange = { },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            enabled = false
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = Modifier.padding(top = 32.dp),
                progress = { preloaderProgress },
                composition = composition
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = "Check your inbox (and spam folder, just in case).\nYour new password awaits!",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            onClick = { uiState.close() }
        ) {
            Text("Close")
        }
    }
}

@ThemePreview
@Composable
private fun ChangePasswordContentPreview() {
    KorenTheme {
        ChangePasswordContent(
            uiState = ChangePasswordUiState.Shown(
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
private fun PasswordResetEmailSentContentPreview() {
    KorenTheme {
        EmailSentContent(
            uiState = ChangePasswordUiState.EmailSent(
                email = "johndoe@gmail.com",
                close = {}
            )
        )
    }
}