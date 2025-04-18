package com.koren.onboarding.ui.create_or_join_family

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R
import kotlinx.serialization.Serializable

@Serializable
object CreateOrJoinFamilyDestination

@Composable
fun CreateOrJoinFamilyScreen(
    viewModel: CreateOrJoinFamilyViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onNavigateToPendingInvitationsScreen: () -> Unit
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
            CreateOrJoinFamilyUiSideEffect.NavigateToOnboarding -> onNavigateToOnboarding()
            CreateOrJoinFamilyUiSideEffect.NavigateToPendingInvitationsScreen -> onNavigateToPendingInvitationsScreen()
        }
    }

    CreateOrJoinFamilyScreenContent(
        uiState = uiState
    )
}

@Composable
private fun CreateOrJoinFamilyScreenContent(
    uiState: CreateOrJoinFamilyUiState
) {
    when (uiState) {
        is CreateOrJoinFamilyUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        is CreateOrJoinFamilyUiState.Shown -> CreateOrJoinFamilyScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun CreateOrJoinFamilyScreenShownContent(
    uiState: CreateOrJoinFamilyUiState.Shown
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.welcome_illustration),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = stringResource(R.string.create_or_join_family_title),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp),
            text = stringResource(R.string.create_or_join_family_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.fillMaxWidth(0.8f),
            onClick = {
                uiState.eventSink(CreateOrJoinFamilyUiEvent.CreateFamily)
            }
        ) {
            Text(text = stringResource(R.string.create_family_button_label))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(0.8f),
            onClick = {
                uiState.eventSink(CreateOrJoinFamilyUiEvent.JoinFamily)
            }
        ) {
            Text(text = stringResource(R.string.join_family_button_label))
        }
    }
}

@ThemePreview
@Composable
fun CreateOrJoinFamilyScreenPreview() {
    KorenTheme {
        CreateOrJoinFamilyScreenContent(
            uiState = CreateOrJoinFamilyUiState.Shown(
                eventSink = {}
            )
        )
    }
}