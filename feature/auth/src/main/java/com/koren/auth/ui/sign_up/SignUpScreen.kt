package com.koren.auth.ui.sign_up

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.auth.R
import com.koren.common.util.Destination
import com.koren.designsystem.components.SimpleSnackbar
import com.koren.designsystem.components.dashedBorder
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
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            isTopBarVisible = true,
            isBottomBarVisible = false
        )
    )

    val uiState by signUpViewModel.state.collectAsStateWithLifecycle()

    SignUpContent(
        uiState = uiState,
        onSignUpSuccess = onSignUpSuccess
    )
}

@Composable
private fun SignUpContent(
    uiState: SignUpUiState,
    onSignUpSuccess: () -> Unit
) {

    when (uiState) {
        is SignUpUiState.NavigateToHome -> LaunchedEffect(Unit) { onSignUpSuccess() }
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
            uiState.eventSink(SignUpEvent.SetImageUri(uri))
        }
    )

    SimpleSnackbar(uiState.genericErrorMessage)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.imageUri != null) {
            AsyncImage(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(uiState.imageUri)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(128.dp)
            ) {
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
                uiState.eventSink(SignUpEvent.FirstNameChanged(it))
            },
            label = { Text(text = stringResource(id = R.string.first_name_label)) }
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.8f),
            value = uiState.lastName,
            onValueChange = {
                uiState.eventSink(SignUpEvent.LastNameChanged(it))
            },
            label = { Text(text = stringResource(id = R.string.last_name_label)) }
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.8f),
            value = uiState.email,
            onValueChange = {
                uiState.eventSink(SignUpEvent.EmailChanged(it))
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
            } else null
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = 8.dp),
            value = uiState.password,
            onValueChange = {
                uiState.eventSink(SignUpEvent.PasswordChanged(it))
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
            } else null
        )

        Button(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.8f),
            enabled = uiState.isSignUpButtonEnabled,
            onClick = {
                uiState.eventSink(SignUpEvent.SignUpButtonClicked)
            }
        ) {
            Text(text = stringResource(id = R.string.sign_up))
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
                text = stringResource(id = R.string.already_have_account_question),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                modifier = Modifier
                    .clickable {

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
            ),
            onSignUpSuccess = {}
        )
    }
}