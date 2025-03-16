@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koren.calendar.ui.add_entry.AddEntryScreen
import com.koren.calendar.ui.calendar.CalendarUiEvent
import com.koren.calendar.ui.calendar.CalendarUiState
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.map.service.LocationUpdateScheduler
import com.koren.navigation.BottomNavigationBar
import com.koren.navigation.KorenNavHost
import com.koren.navigation.MainActivityBottomSheetContent
import com.koren.navigation.MainActivityUiEvent
import com.koren.navigation.MainActivityUiState
import com.koren.navigation.topLevelRoutes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var locationUpdateScheduler: LocationUpdateScheduler


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        locationUpdateScheduler.schedulePeriodicUpdates()

        setContent {
            KorenTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val navController = rememberNavController()
                val scaffoldState = LocalScaffoldStateProvider.current.getScaffoldState().collectAsStateWithLifecycle()
                val snackbarHostState = LocalSnackbarHostState.current

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (scaffoldState.value.isTopBarVisible.not()) return@Scaffold
                        TopAppBar(
                            title = { Text(scaffoldState.value.title) },
                            navigationIcon = {
                                AnimatedVisibility(currentDestination?.route !in topLevelRoutes.map { it.route::class.qualifiedName }) {
                                    IconButton(
                                        onClick = { if (scaffoldState.value.customBackAction != null) scaffoldState.value.customBackAction?.invoke() else navController.popBackStack() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(id = R.string.back)
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (scaffoldState.value.isBottomBarVisible) BottomNavigationBar(navController)
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) { innerPadding ->
                    KorenNavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        mainActivityViewModel = viewModel,
                        onShowSnackbar = { message ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = Short
                            )
                        },
                        setMainActivityBottomSheetContent = { bottomSheetContent ->
                            (uiState as? MainActivityUiState.Success)?.eventSink?.invoke(MainActivityUiEvent.SetBottomSheetContent(bottomSheetContent))
                        }
                    )

                    (uiState as? MainActivityUiState.Success)?.let { uiState ->
                        MainBottomSheet(
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainBottomSheet(
    uiState: MainActivityUiState.Success
) {

    if (uiState.bottomSheetContent is MainActivityBottomSheetContent.None) return

    ModalBottomSheet(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(
                WindowInsetsSides.Vertical
            )
        ),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { uiState.eventSink(MainActivityUiEvent.DismissBottomSheet) }
    ) {
        when (uiState.bottomSheetContent) {
            is MainActivityBottomSheetContent.AddCalendarEntry -> {
                AddEntryScreen(
                    day = uiState.bottomSheetContent.day,
                    onDismiss = { uiState.eventSink(MainActivityUiEvent.DismissBottomSheet) }
                )
            }
            MainActivityBottomSheetContent.None -> Unit
        }
    }
}