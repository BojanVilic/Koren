package com.koren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.koren.auth.service.GoogleAuthService
import com.koren.common.services.UserSession
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.navigation.KorenNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthService: GoogleAuthService

    @Inject
    lateinit var userSession: UserSession

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            KorenTheme {

                val navController = rememberNavController()
                val scaffoldState = LocalScaffoldStateProvider.current.getScaffoldState().collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (scaffoldState.value.isTopBarVisible.not()) return@Scaffold
                        TopAppBar(
                            title = { Text(scaffoldState.value.title) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { if (scaffoldState.value.customBackAction != null) scaffoldState.value.customBackAction?.invoke() else navController.popBackStack() }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.back)
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    KorenNavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        googleAuthService = googleAuthService,
                        mainActivityViewModel = viewModel
                    )
                }
            }
        }
    }
}