package com.koren.activity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.activity.LocationActivity
import com.koren.common.util.Destination
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable
import org.w3c.dom.Text

@Serializable
object ActivityDestination : Destination

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = hiltViewModel()
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = false,
            isBottomBarVisible = true
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActivityScreenContent(uiState = uiState)
}

@Composable
private fun ActivityScreenContent(
    uiState: ActivityUiState
) {
    when (uiState) {
        is ActivityUiState.Empty -> Unit
        is ActivityUiState.Error -> Unit
        is ActivityUiState.Loading -> LoadingContent()
        is ActivityUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
fun ShownContent(uiState: ActivityUiState.Shown) {
    Column {
        LazyColumn {
            items(uiState.activities) { activity ->
                Text("Location Activity ${activity.locationName}")
            }
        }
    }
}

@ThemePreview
@Composable
fun ActivityScreenPreview() {
    KorenTheme {
        ActivityScreenContent(
            uiState = ActivityUiState.Shown(
                activities = listOf(
                    LocationActivity(
                        locationName = "Home"
                    ),
                    LocationActivity(
                        locationName = "Work"
                    )
                ),
                eventSink = {}
            )
        )
    }
}