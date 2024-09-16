package com.koren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.koren.auth.service.GoogleAuthService
import com.koren.common.services.UserSession
import com.koren.designsystem.theme.KorenTheme
import com.koren.navigation.KorenNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthService: GoogleAuthService

    @Inject
    lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KorenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KorenNavHost(
                        modifier = Modifier.padding(innerPadding),
                        googleAuthService = googleAuthService,
                        userSession = userSession
                    )
                }
            }
        }
    }
}