@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koren.designsystem.components.bottom_sheet.rememberBottomSheetNavigator
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.navigation.BottomNavigationBar
import com.koren.navigation.KorenNavHost
import com.koren.navigation.topLevelRoutes
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            KorenTheme {
                val bottomSheetNavigator = rememberBottomSheetNavigator(skipPartiallyExpanded = true)
                val navController = rememberNavController(bottomSheetNavigator)
                val scaffoldState = LocalScaffoldStateProvider.current.getScaffoldState().collectAsStateWithLifecycle()
                val snackbarHostState = LocalSnackbarHostState.current

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (scaffoldState.value.isTopBarVisible.not()) return@Scaffold
                        CenterAlignedTopAppBar(
                            title = { Text(scaffoldState.value.title) },
                            navigationIcon = {
                                AnimatedVisibility(
                                    visible = currentDestination?.route !in topLevelRoutes.map { it.route::class.qualifiedName }
                                            && navController.previousBackStackEntry != null
                                ) {
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
                        bottomSheetNavigator = bottomSheetNavigator
                    )
                }
            }
        }
    }
}