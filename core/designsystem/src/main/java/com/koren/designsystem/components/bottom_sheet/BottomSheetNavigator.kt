@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.designsystem.components.bottom_sheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastForEach
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorState
import androidx.navigation.compose.LocalOwnersProvider
import com.koren.designsystem.components.bottom_sheet.BottomSheetNavigator.Destination
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class BottomSheetNavigatorSheetState(private val sheetState: SheetState) {
    val isVisible: Boolean
        get() = sheetState.isVisible

    val currentValue: SheetValue
        get() = sheetState.currentValue

    val targetValue: SheetValue
        get() = sheetState.targetValue
}

@Composable
fun rememberBottomSheetNavigator(
    skipPartiallyExpanded: Boolean = false,
): BottomSheetNavigator {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    return remember(sheetState) { BottomSheetNavigator(sheetState) }
}

@Navigator.Name("bottomSheet")
class BottomSheetNavigator(
    internal val sheetState: SheetState
) : Navigator<Destination>() {

    internal var sheetEnabled by mutableStateOf(false)
        private set

    private var attached by mutableStateOf(false)

    private val backStack: StateFlow<List<NavBackStackEntry>>
        get() = if (attached) {
            state.backStack
        } else {
            MutableStateFlow(emptyList())
        }

    private val transitionsInProgress: StateFlow<Set<NavBackStackEntry>>
        get() = if (attached) {
            state.transitionsInProgress
        } else {
            MutableStateFlow(emptySet())
        }

    val navigatorSheetState: BottomSheetNavigatorSheetState =
        BottomSheetNavigatorSheetState(sheetState)

    internal var sheetContent: @Composable ColumnScope.() -> Unit = {}
    internal var onDismissRequest: () -> Unit = {}

    private var animateToDismiss: () -> Unit = {}


    internal val sheetInitializer: @Composable () -> Unit = {
        val saveableStateHolder = rememberSaveableStateHolder()
        val transitionsInProgressEntries by transitionsInProgress.collectAsState()

        val retainedEntry by produceState<NavBackStackEntry?>(
            initialValue = null,
            key1 = backStack
        ) {
            backStack
                .transform { backStackEntries ->
                    try {
                        sheetEnabled = false
                    } catch (_: CancellationException) {
                        // We catch but ignore possible cancellation exceptions as we don't want
                        // them to bubble up and cancel the whole produceState coroutine
                    } finally {
                        emit(backStackEntries.lastOrNull())
                    }
                }
                .collect {
                    value = it
                }
        }

        if (retainedEntry != null) {
            val currentOnSheetShown by rememberUpdatedState {
                transitionsInProgressEntries.forEach(state::markTransitionComplete)
            }
            LaunchedEffect(sheetState, retainedEntry) {
                snapshotFlow { sheetState.isVisible }
                    // We are only interested in changes in the sheet's visibility
                    .distinctUntilChanged()
                    // distinctUntilChanged emits the initial value which we don't need
                    .drop(1)
                    .collect { visible ->
                        if (visible) {
                            currentOnSheetShown()
                        }
                    }
            }

            val scope = rememberCoroutineScope()

            LaunchedEffect(key1 = retainedEntry) {
                sheetEnabled = true

                sheetContent = {
                    retainedEntry?.let { retainedEntry ->
                        retainedEntry.LocalOwnersProvider(saveableStateHolder) {
                            val content =
                                (retainedEntry.destination as Destination).content
                            content(retainedEntry)
                        }
                    }

                }
                onDismissRequest = {
                    sheetEnabled = false
                    retainedEntry?.let {
                        state.pop(popUpTo = it, saveState = false)
                    }
                }

                animateToDismiss = {
                    scope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            onDismissRequest()
                        }
                }
            }

            BackHandler {
                animateToDismiss()
            }

        } else {
            LaunchedEffect(key1 = Unit) {
                sheetContent = {}
                onDismissRequest = {}
                animateToDismiss = {}
            }
        }


    }

    override fun onAttach(state: NavigatorState) {
        super.onAttach(state)
        attached = true
    }

    override fun createDestination(): Destination = Destination(
        navigator = this,
        content = {}
    )

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        onDismissRequest()
        entries.fastForEach { entry ->
            state.push(entry)
        }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.pop(popUpTo, savedState)
    }

    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: BottomSheetNavigator,
        internal val content: @Composable ColumnScope.(NavBackStackEntry) -> Unit
    ) : NavDestination(navigator), FloatingWindow
}
