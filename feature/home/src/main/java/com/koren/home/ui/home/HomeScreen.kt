@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.koren.home.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
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
import com.koren.common.models.invitation.Invitation
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.components.DisposableEffectWithLifecycle
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.ui.home.member_details.MemberDetailsScreen
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

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .animateContentSize()
    ) {
        upcomingEventsAndTasks(state)
        familySection(state)
        receivedInvitationsSection(state)
        sentInvitationsSection(state)
        quickActionsSection(state)
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
                    eventSink = {}
                )
            )
        }
    }
}