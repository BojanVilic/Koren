@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.koren.home.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Hail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.koren.common.models.calendar.Day
import com.koren.common.models.calendar.TaskWithUsers
import com.koren.common.models.family.FamilyMemberUserData
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.components.Scrim
import com.koren.designsystem.models.ActionItem
import com.koren.designsystem.models.IconResource
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
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
    sentInvitations: () -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    openAddCalendarEntry: (Day) -> Unit,
    openMemberDetails: (userId: String) -> Unit,
    navigateToChat: () -> Unit
) {

    LocalScaffoldStateProvider.current
        .setScaffoldState(
            state = ScaffoldState(
                isTopBarVisible = false,
                isBottomBarVisible = true
            )
        )

    val state by homeViewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = homeViewModel
    ) { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.ShowMessage -> onShowSnackbar(sideEffect.message)
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
            is HomeSideEffect.NavigateToChat -> navigateToChat()
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
fun HomeScaffoldWithExpandingFab(
    state: HomeUiState.Shown
) {

    val actions = listOf(
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Add),
            text = "Invite",
            onClick = { state.eventSink(HomeEvent.NavigateToInviteFamilyMember) }
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Email),
            text = "Chat",
            onClick = { state.eventSink(HomeEvent.NavigateToChat) }
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.DateRange),
            text = "Schedule",
            onClick = { state.eventSink(HomeEvent.OpenAddCalendarEntry) }
        ),
        ActionItem(
            icon = IconResource.Vector(Icons.Default.Hail),
            text = "Request Pick Up",
            onClick = { state.eventSink(HomeEvent.RequestPickUpClicked) }
        )
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
    val hapticFeedback = LocalHapticFeedback.current
    val rotationAngle by animateFloatAsState(
        targetValue = if (actionsOpen) 225f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    FloatingActionButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
            onClick()
        }
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
    val hapticFeedback = LocalHapticFeedback.current

    FloatingActionButton(
        modifier = modifier,
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                        FamilyMemberUserData(
                            userData = UserData(
                                id = "1",
                                displayName = "John Doe",
                                profilePictureUrl = "https://example.com/john.jpg"
                            ),
                            distance = 2000,
                            goingHome = true
                        ),
                        FamilyMemberUserData(
                            userData = UserData(
                                id = "2",
                                displayName = "Jane Smith",
                                profilePictureUrl = "https://example.com/jane.jpg"
                            ),
                            distance = 5660,
                            goingHome = false
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