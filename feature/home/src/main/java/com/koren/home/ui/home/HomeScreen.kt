@file:OptIn(ExperimentalPermissionsApi::class)

package com.koren.home.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.invitation.getExpiryText
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.DateUtils.toRelativeTime
import com.koren.designsystem.components.ActionButton
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.components.StyledStringResource
import com.koren.designsystem.models.ActionItem
import com.koren.designsystem.models.IconResource
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    inviteFamilyMember: () -> Unit,
    createFamily: () -> Unit,
    sentInvitations: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit
) {

    val scaffoldStateProvider = LocalScaffoldStateProvider.current
    DisposableEffectWithLifecycle(
        onStart = { scaffoldStateProvider.setScaffoldState(ScaffoldState(isTopBarVisible = false, isBottomBarVisible = true)) },
        onResume = { scaffoldStateProvider.setScaffoldState(ScaffoldState(isTopBarVisible = false, isBottomBarVisible = true)) }
    )

    val state by homeViewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = homeViewModel
    ) { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.ShowError -> onShowSnackbar(sideEffect.message)
            is HomeSideEffect.NavigateToCreateFamily -> createFamily()
            is HomeSideEffect.NavigateToInviteFamilyMember -> inviteFamilyMember()
            is HomeSideEffect.NavigateToSentInvitations -> sentInvitations()
        }
    }

    HomeContent(
        state = state
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState
) {
    when (state) {
        is HomeUiState.Loading -> LoadingContent()
        is HomeUiState.Shown -> ShownContent(
            state = state
        )
    }
}

@Composable
private fun ShownContent(
    state: HomeUiState.Shown
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationsPermission = rememberPermissionState(POST_NOTIFICATIONS)

        LaunchedEffect(notificationsPermission.status.isGranted) {
            if (notificationsPermission.status.isGranted.not())
                notificationsPermission.launchPermissionRequest()
        }
    }

    val actions = listOf(
        ActionItem(
            icon = IconResource.Drawable(R.drawable.create_family),
            text = "Create\nfamily",
            onClick = { state.eventSink(HomeEvent.NavigateToCreateFamily) }
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Add),
            text = "Invite",
            onClick = { state.eventSink(HomeEvent.NavigateToInviteFamilyMember) }
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Email),
            text = "Chat",
            onClick = {}
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.DateRange),
            text = "Schedule",
            onClick = {}
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Notifications),
            text = "Reminder",
            onClick = {}
        ),
        ActionItem(
            icon = IconResource.Drawable(R.drawable.remove_person),
            text = "Remove",
            onClick = {}
        ),
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        if (state.familyMembers.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(R.string.family_members)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
            ) {
                LazyRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(state.familyMembers) { member ->
                        FamilyMember(member = member)
                    }
                    item {
                        FilledTonalIconButton(
                            onClick = { state.eventSink(HomeEvent.NavigateToInviteFamilyMember) }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(64.dp),
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add family member"
                            )
                        }
                    }
                }
            }
        }

        if (state.receivedInvitations.isNotEmpty()) {
            Column {
                state.receivedInvitations.forEach { invitation ->
                    ReceivedInvitationCard(
                        invitation = invitation,
                        invitationCodeText = state.invitationCodeText,
                        acceptInvitation = { code -> state.eventSink(HomeEvent.AcceptInvitation(invitation, code)) },
                        declineInvitation = { state.eventSink(HomeEvent.DeclineInvitation(invitation.id)) },
                        onInvitationCodeChanged = { state.eventSink(HomeEvent.InvitationCodeChanged(it)) },
                        invitationCodeError = state.invitationCodeError
                    )
                }
            }
        }

        AnimatedVisibility(state.sentInvitations.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                onClick = { state.eventSink(HomeEvent.NavigateToSentInvitations) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val invitationNumberStyle = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    StyledStringResource(
                        stringRes = R.string.sent_invitations,
                        formatArgs = listOf(
                            "(${state.sentInvitations.size})" to invitationNumberStyle
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "Actions"
        )

        Card {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .height(IntrinsicSize.Max)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { actionItem ->
                    ActionButton(actionItem)
                }
            }
        }
    }
}

@Composable
private fun FamilyMember(member: UserData) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .data(member.profilePictureUrl)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = member.displayName,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReceivedInvitationCard(
    invitation: Invitation,
    invitationCodeText: String,
    acceptInvitation: (String) -> Unit,
    declineInvitation: () -> Unit,
    onInvitationCodeChanged: (String) -> Unit,
    invitationCodeError: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            text = invitation.status.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                text = "You have a new invitation from ${invitation.senderName} to join ${invitation.familyName}.",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            DividerWithText(
                modifier = Modifier.padding(vertical = 16.dp),
                text = invitation.createdAt.toRelativeTime()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = invitationCodeText,
                    onValueChange = {
                        if (it.length <= 6) onInvitationCodeChanged(it.uppercase())
                    },
                    label = { Text("Invitation code") },
                    supportingText = { Text(invitationCodeError) },
                    isError = invitationCodeError.isNotEmpty(),
                    visualTransformation = InvitationCodeVisualTransformation()
                )

                Spacer(modifier = Modifier.width(32.dp))

                FilledIconButton(
                    onClick = { acceptInvitation(invitationCodeText) },
                    enabled = invitationCodeText.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Accept invitation"
                    )
                }

                FilledIconButton(
                    onClick = { declineInvitation() },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline invitation"
                    )
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = invitation.getExpiryText(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

class InvitationCodeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 6) text.text.substring(0, 6) else text.text
        val upperCase = trimmed.uppercase()
        val spaced = buildString {
            upperCase.forEachIndexed { index, char ->
                append(char)
                if ((index + 1) % 3 == 0 && index < upperCase.length - 1) {
                    append(" ")
                }
            }
        }

        return TransformedText(AnnotatedString(spaced), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                val spacedOffset = offset + (offset - 1) / 3
                return minOf(spacedOffset, spaced.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return offset
                val originalOffset = offset - (offset - 1) / 4 // Divide by 4 because of added space
                return minOf(originalOffset, upperCase.length)
            }
        })
    }
}

@Composable
fun DividerWithText(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.weight(0.6f),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@ThemePreview
@Composable
fun HomePreview() {
    KorenTheme {
        HomeContent(
            state = HomeUiState.Shown(
                receivedInvitations = listOf(
                    Invitation(
                        id = "1",
                        senderId = "sender1",
                        status = InvitationStatus.PENDING,
                        expirationDate = 1630000000000,
                        recipientEmail = ""
                    )
                ),
                sentInvitations = listOf(
                    Invitation(
                        status = InvitationStatus.ACCEPTED
                    ),
                    Invitation(
                        status = InvitationStatus.PENDING,
                        createdAt = 1735958071,
                        expirationDate = 1735958177,
                        recipientEmail = "johndoe@email.com"
                    ),
                    Invitation(
                        status = InvitationStatus.DECLINED,
                        recipientEmail = "johndoe@email.com"
                    ),
                    Invitation(
                        status = InvitationStatus.EXPIRED,
                        recipientEmail = "johndoe@email.com"
                    )
                ),
                eventSink = {}
            )
        )
    }
}