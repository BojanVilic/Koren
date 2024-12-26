package com.koren.home.ui.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.UserData
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import com.koren.home.ui.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination : Destination

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    logOut: () -> Unit,
    createFamily: () -> Unit
) {

    val currentUser by homeViewModel.currentUser.collectAsStateWithLifecycle()

    HomeContent(
        logOut = logOut,
        createFamily = createFamily,
        currentUser = currentUser
    )
}

@Composable
private fun HomeContent(
    logOut: () -> Unit,
    createFamily: () -> Unit,
    currentUser: UserData?
) {
    Column {
        Text(
            text = stringResource(R.string.welcome_back_label, currentUser?.displayName?: ""),
            style = MaterialTheme.typography.labelLarge
        )

        Button(
            onClick = logOut
        ) {
            Text(text = "Log out")
        }

        Button(
            onClick = { createFamily() }
        ) {
            Text("Create a family")
        }
    }
}

@ThemePreview
@Composable
fun HomePreview() {
    KorenTheme {
        HomeContent(
            logOut = {},
            createFamily = {},
            currentUser = UserData(
                id = "",
                displayName = "John Doe",
                email = ""
            )
        )
    }
}