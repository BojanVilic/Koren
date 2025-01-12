package com.koren.auth.ui.sign_up

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.auth.R
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.Destination
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable

@Serializable
data object SignUpScreen : Destination

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            title = stringResource(R.string.sign_up),
            isTopBarVisible = true,
            isBottomBarVisible = false
        )
    )

    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = signUpViewModel
    ) { sideEffect ->
        when (sideEffect) {
            is SignUpUiSideEffect.NavigateBack -> onNavigateBack()
            is SignUpUiSideEffect.NavigateToHome -> onSignUpSuccess()
            is SignUpUiSideEffect.ShowGenericMessage -> onShowSnackbar(sideEffect.message)
        }
    }

    SignUpContent(
        uiState = uiState
    )
}

@Composable
private fun SignUpContent(
    uiState: SignUpUiState
) {

    when (uiState) {
        is SignUpUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
private fun ShownContent(
    uiState: SignUpUiState.Shown
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                uiState.eventSink(SignUpUiEvent.SetImageUri(it))
            }
        }
    )

    SimpleSnackbar(uiState.genericErrorMessage)

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(128.dp)
        ) {
            if (uiState.imageUri != null) {
                AsyncImage(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(uiState.imageUri)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add photo",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (uiState.imageUri != null) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .offset {
                            IntOffset(-16, -16)
                        }
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    painter = painterResource(R.drawable.ic_replace),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .offset {
                            IntOffset(-16, -16)
                        }
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.8f),
            value = uiState.firstName,
            onValueChange = {
                uiState.eventSink(SignUpUiEvent.FirstNameChanged(it))
            },
            label = { Text(text = stringResource(id = R.string.first_name_label)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.8f),
            value = uiState.lastName,
            onValueChange = {
                uiState.eventSink(SignUpUiEvent.LastNameChanged(it))
            },
            label = { Text(text = stringResource(id = R.string.last_name_label)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.8f),
            value = uiState.email,
            onValueChange = {
                uiState.eventSink(SignUpUiEvent.EmailChanged(it))
            },
            label = { Text(text = stringResource(id = R.string.email_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null
                )
            },
            supportingText = if (uiState.emailErrorMessage.isNotBlank()) {
                {
                    AnimatedVisibility(uiState.emailErrorMessage.isNotBlank()) {
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .animateContentSize(),
                            text = uiState.emailErrorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else null,
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
                uiState.eventSink(SignUpUiEvent.PasswordChanged(it))
            },
            label = { Text(text = stringResource(id = R.string.password_label)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password),
                    contentDescription = null
                )
            },
            supportingText = if (uiState.passwordErrorMessage.isNotBlank()) {
                {
                    AnimatedVisibility(uiState.passwordErrorMessage.isNotBlank()) {
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .animateContentSize(),
                            text = uiState.passwordErrorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else null,
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
                        uiState.eventSink(SignUpUiEvent.ShowPasswordClicked)
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
            enabled = uiState.isSignUpButtonEnabled,
            onClick = {
                uiState.eventSink(SignUpUiEvent.SignUpButtonClicked)
            }
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = stringResource(id = R.string.already_have_account_question),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                modifier = Modifier
                    .clickable {
                        uiState.eventSink(SignUpUiEvent.SignInClicked)
                    },
                text = stringResource(id = R.string.sign_in),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@ThemePreview
@Composable
fun SignUpPreview() {
    KorenTheme {
        SignUpContent(
            uiState = SignUpUiState.Shown(
                eventSink = {}
            )
        )
    }
}