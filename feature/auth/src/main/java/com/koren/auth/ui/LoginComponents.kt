package com.koren.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.koren.auth.R
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onSignInSuccess: () -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            isTopBarVisible = false,
            isBottomBarVisible = false
        )
    )

    val scope = rememberCoroutineScope()

    SignInContent(
        modifier = modifier,
        onGoogleSignInClicked = {
            scope.launch {
                val result = authViewModel.signIn()
                when {
                    result.isSuccess -> onSignInSuccess()
                    result.isFailure -> Timber.e("Sign in failed: ${result.exceptionOrNull()}")
                }
            }
        }
    )
}

@Composable
private fun SignInContent(
    modifier: Modifier = Modifier,
    onGoogleSignInClicked: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(0.2f)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.background
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .align(Alignment.Start),
                    text = "Welcome back!",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
                        .align(Alignment.Start),
                    text = "Please, sign in to continue.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(0.8f),
                    value = "",
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.email_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null
                        )
                    }
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 16.dp),
                    value = "",
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.password_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_password),
                            contentDescription = null
                        )
                    }
                )

                Button(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(0.8f),
                    onClick = onGoogleSignInClicked
                ) {
                    Text(text = stringResource(id = R.string.sign_in))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.or_label),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    HorizontalDivider(Modifier.weight(1f))
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    onClick = onGoogleSignInClicked
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = stringResource(id = R.string.google_sign_in_title))
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = stringResource(id = R.string.no_account_question),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = stringResource(id = R.string.sign_up),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

    }
}

@ThemePreview
@Composable
fun SignInPreview() {
    KorenTheme {
        SignInContent(
            onGoogleSignInClicked = {}
        )
    }
}