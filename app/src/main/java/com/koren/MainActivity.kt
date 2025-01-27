package com.koren

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequest.Companion.MIN_PERIODIC_INTERVAL_MILLIS
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.map.service.LocationWorker
import com.koren.navigation.BottomNavigationBar
import com.koren.navigation.KorenNavHost
import com.koren.navigation.topLevelRoutes
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        val locationWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<LocationWorker>(repeatInterval = Duration.ofMinutes(15))
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "LocationWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            locationWorkRequest
        )

        setContent {
            KorenTheme {

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
                        }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("location_updates", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}