@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.calendar

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.calendar.Day
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
    onShowSnackbar: suspend (message: String) -> Unit,
    openDayDetails: (Day) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = true,
            title = "Calendar",
            isBottomBarVisible = false
        )
    )

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is CalendarUiSideEffect.ShowSnackbar -> onShowSnackbar(uiSideEffect.message)
            is CalendarUiSideEffect.OpenDayDetails -> openDayDetails(uiSideEffect.day)
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
    CalendarUI(
        uiState = uiState
    )
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