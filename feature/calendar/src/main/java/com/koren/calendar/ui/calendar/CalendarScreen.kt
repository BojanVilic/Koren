@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.calendar.ui.calendar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.calendar.ui.day_details.DayDetailsScreen
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CalendarDestination

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = true,
            title = "Calendar",
            isBottomBarVisible = false
        )
    )

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            if (it == SheetValue.Hidden) {
                (uiState as? CalendarUiState.Shown)?.let { state ->
                    state.eventSink(CalendarUiEvent.ResetCalendarBottomSheetContent)
                }
                true
            } else true
        }
    )

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is CalendarUiSideEffect.ShowSnackbar -> onShowSnackbar(uiSideEffect.message)
        }
    }

    CalendarScreenContent(
        uiState = uiState,
        sheetState = sheetState
    )
}

@Composable
private fun CalendarScreenContent(
    uiState: CalendarUiState,
    sheetState: SheetState
) {
    when (uiState) {
        is CalendarUiState.Loading -> CircularProgressIndicator()
        is CalendarUiState.Shown -> CalendarScreenShownContent(
            uiState = uiState,
            sheetState = sheetState
        )
    }
}

@Composable
private fun CalendarScreenShownContent(
    uiState: CalendarUiState.Shown,
    sheetState: SheetState
) {

    val coroutineScope = rememberCoroutineScope()

    if (uiState.calendarBottomSheetContent !is CalendarBottomSheetContent.None) {
        ModalBottomSheet(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Vertical
                )
            ),
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                }
            }
        ) {
            when (uiState.calendarBottomSheetContent) {
                is CalendarBottomSheetContent.DayDetails -> DayDetailsScreen(
                    day = uiState.calendarBottomSheetContent.day,
                    onDismiss = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    }
                )
                else -> Unit
            }
        }
    }

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
            ),
            sheetState = rememberModalBottomSheetState()
        )
    }
}