package com.koren.calendar.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
object CalendarDestination

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(

        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is CalendarUiSideEffect.ShowSnackbar -> onShowSnackbar(uiSideEffect.message)
        }
    }

    CalendarScreenContent(
        uiState = uiState
    )
}

@Composable
private fun CalendarScreenContent(
    uiState: CalendarUiState
) {
    when (uiState) {
        is CalendarUiState.Loading -> CircularProgressIndicator()
        is CalendarUiState.Shown -> CalendarScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun CalendarScreenShownContent(
    uiState: CalendarUiState.Shown
) {
    CalendarUI()
}

@ThemePreview
@Composable
fun CalendarScreenPreview() {
    KorenTheme {
        CalendarScreenContent(
            uiState = CalendarUiState.Shown(
                eventSink = {}
            )
        )
    }
}