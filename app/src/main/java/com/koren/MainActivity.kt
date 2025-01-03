package com.koren

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koren.auth.service.GoogleAuthService
import com.koren.common.services.UserSession
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.LocalSnackbarHostState
import com.koren.navigation.BottomNavigationBar
import com.koren.navigation.KorenNavHost
import com.koren.navigation.topLevelRoutes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.net.URLDecoder
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthService: GoogleAuthService

    @Inject
    lateinit var userSession: UserSession

    private val viewModel: MainActivityViewModel by viewModels()


    private fun handleInvitation(familyId: String, invitationCode: String) {
        Timber.d("Family ID: $familyId, Invitation Code: $invitationCode")
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        val data: Uri? = intent?.data
        val encodedQuery = data?.query // Get the raw query string (encoded)

// Decode the query string (if not null)
        val decodedQuery = encodedQuery?.let { URLDecoder.decode(it, "UTF-8") }

        if (decodedQuery != null) {
            val familyId = Uri.parse("?$decodedQuery").getQueryParameter("familyId")
            val invitationCode = Uri.parse("?$decodedQuery").getQueryParameter("invCode")

            println("Family ID: $familyId")
            println("Invitation Code: $invitationCode")

            if (familyId != null && invitationCode != null) {
                handleInvitation(familyId, invitationCode)
            } else {
                showError("Invalid invitation link")
            }
        }


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
                        googleAuthService = googleAuthService,
                        mainActivityViewModel = viewModel
                    )
                }
            }
        }
    }
}