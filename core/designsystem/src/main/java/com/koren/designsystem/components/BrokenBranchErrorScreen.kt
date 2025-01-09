package com.koren.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.koren.designsystem.R
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview

@Composable
fun BrokenBranchErrorScreen(
    errorMessage: String
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_screen))
    val preloaderProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.padding(top = 32.dp),
            progress = { preloaderProgress },
            composition = composition
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = errorMessage,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@ThemePreview
@Composable
fun BrokenBranchErrorScreenPreview() {
    KorenTheme {
        BrokenBranchErrorScreen(
            errorMessage = "You are already a member of the family. To create a new family, please leave your current family from the account section in the app."
        )
    }
}