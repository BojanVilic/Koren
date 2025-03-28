package com.koren.auth.ui.sign_in

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.auth.R
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
data object SignInDestination

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel = hiltViewModel(),
    onSignInSuccess: () -> Unit,
    navigateToSignUp: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            isTopBarVisible = false,
            isBottomBarVisible = false
        )
    )

    val uiState by signInViewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel = signInViewModel) { sideEffect ->
        when (sideEffect) {
            is SignInUiSideEffect.ShowError -> onShowSnackbar(sideEffect.message)
            is SignInUiSideEffect.NavigateToHome -> onSignInSuccess()
            is SignInUiSideEffect.NavigateToSignUp -> navigateToSignUp()
        }
    }

    SignInContent(uiState = uiState)
}

@Composable
private fun SignInContent(
    uiState: SignInUiState
) {

    when (uiState) {
        is SignInUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
private fun ShownContent(
    uiState: SignInUiState.Shown
) {

    SimpleSnackbar(uiState.errorMessage)

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
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
                    value = uiState.email,
                    onValueChange = {
                        uiState.eventSink(SignInUiEvent.EmailChanged(it))
                    },
                    label = { Text(text = stringResource(id = R.string.email_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 8.dp),
                    value = uiState.password,
                    onValueChange = {
                        uiState.eventSink(SignInUiEvent.PasswordChanged(it))
                    },
                    label = { Text(text = stringResource(id = R.string.password_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_password),
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                uiState.eventSink(SignInUiEvent.ShowPasswordClicked)
                            },
                            painter = painterResource(
                                id = if (uiState.showPassword) {
                                    R.drawable.ic_pw_visible
                                } else {
                                    R.drawable.ic_pw_hidden
                                }
                            ),
                            contentDescription = if (uiState.showPassword) {
                                stringResource(id = R.string.show_password)
                            } else {
                                stringResource(id = R.string.hide_password)
                            }
                        )
                    },
                    visualTransformation = if (uiState.showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    }
                )

                Button(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(0.8f),
                    enabled = uiState.isSignInButtonEnabled,
                    onClick = {
                        uiState.eventSink(SignInUiEvent.SignInClicked)
                    }
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
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = {
                        uiState.eventSink(SignInUiEvent.GoogleSignIn)
                    }
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
                        modifier = Modifier
                            .clickable {
                                uiState.eventSink(SignInUiEvent.NavigateToSignUp)
                            },
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
            uiState = SignInUiState.Shown(
                eventSink = {}
            )
        )
    }
}