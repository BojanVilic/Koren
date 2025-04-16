@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.koren.home.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.koren.common.models.calendar.Day
import com.koren.common.models.calendar.TaskWithUsers
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.RemovePerson
import com.koren.designsystem.models.ActionItem
import com.koren.designsystem.models.IconResource
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
object HomeDestination

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    inviteFamilyMember: () -> Unit,
    createFamily: () -> Unit,
    sentInvitations: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    openAddCalendarEntry: (Day) -> Unit,
    openMemberDetails: (userId: String) -> Unit
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
            is HomeSideEffect.OpenAddCalendarEntry -> openAddCalendarEntry(
                Day(
                    dayOfMonth = 16,
                    dayOfWeek = DayOfWeek.SUNDAY,
                    localDate = LocalDate.now(),
                    tasks = emptyList(),
                    events = emptyList()
                )
            )
            is HomeSideEffect.OpenMemberDetails -> openMemberDetails(sideEffect.member.id)
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
        is HomeUiState.Shown -> HomeScaffoldWithExpandingFab(
            state = state
        )
    }
}

@Composable
private fun ShownContent(
    state: HomeUiState.Shown,
    scaffoldPadding: PaddingValues
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationsPermission = rememberPermissionState(POST_NOTIFICATIONS)

        LaunchedEffect(notificationsPermission.status.isGranted) {
            if (notificationsPermission.status.isGranted.not())
                notificationsPermission.launchPermissionRequest()
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(scaffoldPadding)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .animateContentSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        glanceItems(state)
        familySection(state)
        receivedInvitationsSection(state)
        sentInvitationsSection(state)
    }
}

@Composable
fun Scrim(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    )
}

@Composable
fun HomeScaffoldWithExpandingFab(
    state: HomeUiState.Shown
) {

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
            onClick = { state.eventSink(HomeEvent.OpenAddCalendarEntry) }
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Notifications),
            text = "Reminder",
            onClick = {}
        ),
        ActionItem(
            icon = IconResource.Vector(KorenIcons.RemovePerson),
            text = "Remove",
            onClick = {}
        ),
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0,0 ,0 ,0)
    ) { paddingValues ->

        ShownContent(state, paddingValues)

        Box(modifier = Modifier.fillMaxSize()) {

            AnimatedVisibility(
                visible = state.actionsOpen,
                enter = fadeIn(animationSpec = tween(durationMillis = 800)),
                exit = fadeOut(animationSpec = tween(durationMillis = 600))
            ) {
                Scrim(onClick = { state.eventSink(HomeEvent.ActionsFabClicked) })
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = paddingValues.calculateBottomPadding() + 16.dp,
                        end = 16.dp
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    actions.forEachIndexed { index, action ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(state.actionsOpen) {
                            if (state.actionsOpen) {
                                delay(((actions.size - index) * 10L))
                                visible = true
                            } else {
                                visible = false
                            }
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter = scaleIn(animationSpec = tween(durationMillis = 200)),
                            exit = scaleOut(animationSpec = tween(durationMillis = 150))
                        ) {
                            MiniFabItem(
                                modifier = Modifier.fillMaxWidth(),
                                actionItem = action,
                                onClick = {
                                    state.eventSink(HomeEvent.ActionsFabClicked)
                                }
                            )
                        }
                    }
                }

                AnimatedFabIcon(
                    actionsOpen = state.actionsOpen,
                    onClick = { state.eventSink(HomeEvent.ActionsFabClicked) }
                )
            }
        }
    }
}

@Composable
fun AnimatedFabIcon(
    actionsOpen: Boolean,
    onClick: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (actionsOpen) 45f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.rotate(rotationAngle),
            imageVector = Icons.Filled.Add,
            contentDescription = if (actionsOpen) "Collapse Actions" else "Expand Actions"
        )
    }
}

@Composable
fun MiniFabItem(
    modifier: Modifier = Modifier,
    actionItem: ActionItem,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = {
            actionItem.onClick()
            onClick()
        }
    ) {
        Row(
            modifier = modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actionItem.IconComposable()
            Text(
                text = actionItem.text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
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
        Surface {
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
                    familyMembers = listOf(
                        UserData(
                            id = "1",
                            displayName = "John Doe",
                        ),
                        UserData(
                            id = "2",
                            displayName = "Jane Doe",
                        )
                    ),
                    freeDayNextItem = NextItem.TaskItem(
                        TaskWithUsers(
                            taskId = "1",
                            title = "Task 1",
                            description = "Description 1",
                            taskTimestamp = 1630000000000,
                            completed = false,
                            creator = UserData(
                                id = "1",
                                displayName = "John Doe"
                            ),
                            assignee = UserData(
                                id = "2",
                                displayName = "Jane Doe"
                            )
                        )
                    ),
                    eventSink = {}
                )
            )
        }
    }
}