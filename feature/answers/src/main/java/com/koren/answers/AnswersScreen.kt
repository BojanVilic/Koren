package com.koren.answers

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable
import com.koren.designsystem.theme.*
import com.koren.common.util.*
import androidx.compose.material3.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.designsystem.components.LoadingContent

@Serializable
object AnswersDestination

@Composable
fun AnswersScreen(
    viewModel: AnswersViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {
    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(

        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { sideEffect ->
        when (sideEffect) {
            else -> Unit
        }
    }

    AnswersScreenContent(uiState)
}

@Composable
private fun AnswersScreenContent(uiState: AnswersUiState) {
    when (uiState) {
        is AnswersUiState.Loading -> LoadingContent()
        is AnswersUiState.Shown -> AnswersScreenShownContent(uiState)
    }
}

@Composable
private fun AnswersScreenShownContent(uiState: AnswersUiState.Shown) {

}

@ThemePreview
@Composable
private fun AnswersScreenPreview() {
    KorenTheme {
        AnswersScreenContent(
            AnswersUiState.Shown(
                eventSink = {}
            )
        )
    }
}