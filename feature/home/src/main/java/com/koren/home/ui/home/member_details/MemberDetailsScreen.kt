@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.home.member_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.calendar.Task
import com.koren.common.models.calendar.TaskTimeRange
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.DateUtils.toHumanReadableDateTime
import com.koren.designsystem.icon.CallHome
import com.koren.designsystem.icon.Circle
import com.koren.designsystem.icon.CircleCheck
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.MapSelected
import com.koren.designsystem.icon.Task
import com.koren.designsystem.icon.Warning
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class MemberDetailsDestination(
    val userId: String
)

@Composable
fun MemberDetailsScreen(
    viewModel: MemberDetailsViewModel = hiltViewModel(),
    userId: String,
    navigateAndFindOnMap: (userId: String) -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.init(userId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is MemberDetailsUiSideEffect.NavigateAndFindOnMap -> navigateAndFindOnMap(uiSideEffect.userId)
            is MemberDetailsUiSideEffect.ShowSnackbarMessage -> Unit
        }
    }

    MemberDetailsScreenContent(
        uiState = uiState
    )
}

@Composable
private fun MemberDetailsScreenContent(
    uiState: MemberDetailsUiState
) {
    when (uiState) {
        is MemberDetailsUiState.Loading -> CircularProgressIndicator()
        is MemberDetailsUiState.Shown -> MemberDetailsScreenShownContent(uiState = uiState)
        is MemberDetailsUiState.SelfDetails -> SelfDetailsContent()
    }
}

@Composable
private fun MemberDetailsScreenShownContent(
    uiState: MemberDetailsUiState.Shown
) {

    if (uiState.showViewAssignedTasksDialog) {
        AssignedTasksDialog(uiState = uiState)
    }

    Column(
        modifier = Modifier
            .clip(BottomSheetDefaults.ExpandedShape)
            .background(MaterialTheme.colorScheme.inverseSurface)
    ) {
        Row(
            modifier = Modifier
                .clip(BottomSheetDefaults.ExpandedShape)
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(MaterialTheme.colorScheme.inverseSurface),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = KorenIcons.MapSelected,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inverseOnSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Crossfade(
                targetState = uiState.distanceText
            ) { distanceText ->
                Text(
                    text = distanceText,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Column(
            modifier = Modifier
                .clip(BottomSheetDefaults.ExpandedShape)
                .fillMaxWidth()
                .background(BottomSheetDefaults.ContainerColor)
        ) {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            uiState.options.forEach { option ->
                Option(
                    icon = option.icon,
                    title = option.title,
                    description = option.description,
                    isEnabled = option.isEnabled,
                    onClick = {
                        if (option.isEnabled) {
                            uiState.eventSink(option.event)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Option(
    icon: ImageVector,
    title: String,
    description: String?,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val contentAlpha = if (isEnabled) 1f else 0.5f

    Row(
        modifier = Modifier
            .alpha(contentAlpha)
            .clickable(enabled = isEnabled) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedVisibility(!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SelfDetailsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetDefaults.DragHandle()
        Text(
            text = getRandomMessageAndEmoji(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

fun getRandomMessageAndEmoji(): String {
    val messagesAndEmojis = listOf(
        "Remember, spoons are just tiny shovels. 🥄",
        "Are your socks mismatched? 🧦",
        "Did you check behind the couch? 🕵️‍♂️",
        "You're the captain of this ship! 🚢",
        "Looking good, you! 😎",
        "You're the star of the show! 🌟",
        "Making things happen, as always! 🚀",
        "You're the boss! 👑",
        "You got this! 💪",
    )
    return messagesAndEmojis.random()
}

@Composable
fun AssignedTasksDialog(
    uiState: MemberDetailsUiState.Shown
) {
    AlertDialog(
        onDismissRequest = { uiState.eventSink(MemberDetailsUiEvent.DismissTasksDialog) },
        title = {
            Text(text = "Tasks for ${uiState.member.displayName}")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TaskTimeRangeChipSelector(
                    selectedRange = uiState.selectedTimeRange,
                    onRangeSelected = {
                        uiState.eventSink(MemberDetailsUiEvent.SelectTimeRange(it))
                    }
                )

                if (uiState.assignedTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks assigned.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.assignedTasks, key = { it.taskId }) { task ->
                            DialogTaskItem(task = task)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                uiState.eventSink(MemberDetailsUiEvent.DismissTasksDialog)
            }) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DialogTaskItem(
    task: Task,
    modifier: Modifier = Modifier
) {
    var currentTimeMillis by remember { mutableLongStateOf(Instant.now().toEpochMilli()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            currentTimeMillis = Instant.now().toEpochMilli()
        }
    }

    val isOverdue by rememberUpdatedState(newValue = task.taskTimestamp in 1..<currentTimeMillis && !task.completed)

    val offsetY by animateDpAsState(
        targetValue = if (isOverdue) 12.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearOutSlowInEasing
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        AnimatedVisibility(
            visible = isOverdue,
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearOutSlowInEasing
                ),
                initialOffsetY = { it / 2 }
            ) + fadeIn(animationSpec = tween(durationMillis = 200)),
            exit = slideOutVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                ),
                targetOffsetY = { -it / 2 }
            ) + fadeOut(animationSpec = tween(durationMillis = 150))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Card(
                    modifier = Modifier.offset {
                        IntOffset(0, offsetY.roundToPx())
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 6.dp,
                            end = 6.dp,
                            bottom = 16.dp,
                            top = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = KorenIcons.Warning,
                            contentDescription = "Warning Icon",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Overdue",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = if (task.completed) KorenIcons.CircleCheck else KorenIcons.Circle
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = icon,
                    contentDescription = if (task.completed) "Task completed" else "Task not completed",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val formattedDateTime = task.taskTimestamp.toHumanReadableDateTime(atLocalTimeZone = true)
                    if (formattedDateTime.isNotBlank()) {
                        Text(
                            text = "Due: $formattedDateTime",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskTimeRangeChipSelector(
    selectedRange: TaskTimeRange,
    onRangeSelected: (TaskTimeRange) -> Unit
) {
    val ranges = remember {
        listOf(
            TaskTimeRange.Next24Hours,
            TaskTimeRange.Next7Days,
            TaskTimeRange.Next14Days,
            TaskTimeRange.Next30Days
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ranges.forEach { rangeOption ->
            val isSelected = rangeOption == selectedRange
            FilterChip(
                selected = isSelected,
                onClick = { onRangeSelected(rangeOption) },
                label = { Text(rangeToText(rangeOption)) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Selected",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

private fun rangeToText(range: TaskTimeRange): String {
    return when (range) {
        TaskTimeRange.Next24Hours -> "24 Hours"
        TaskTimeRange.Next7Days -> "7 Days"
        TaskTimeRange.Next14Days -> "14 Days"
        TaskTimeRange.Next30Days -> "30 Days"
    }
}

@ThemePreview
@Composable
fun MemberDetailsScreenShownContentPreview() {
    KorenTheme {
        Surface {
            MemberDetailsScreenShownContent(
                uiState = MemberDetailsUiState.Shown(
                    distanceText = "450m away",
                    options = listOf(
                        MemberDetailsOption(
                            icon = KorenIcons.CallHome,
                            title = "Call home",
                            event = MemberDetailsUiEvent.CallHome,
                            isEnabled = false,
                            description = "Request sent"
                        ),
                        MemberDetailsOption(
                            icon = KorenIcons.MapSelected,
                            title = "Find on map",
                            event = MemberDetailsUiEvent.FindOnMap
                        ),
                        MemberDetailsOption(
                            icon = KorenIcons.Task,
                            title = "View assigned tasks",
                            event = MemberDetailsUiEvent.ViewAssignedTasks
                        ),
                        MemberDetailsOption(
                            icon = Icons.Default.Edit,
                            title = "Edit role",
                            event = MemberDetailsUiEvent.EditRole
                        )
                    ),
                    eventSink = {}
                )
            )
        }
    }
}

@ThemePreview
@Composable
private fun AssignedTasksDialogPreview() {
    KorenTheme {
        AssignedTasksDialog(
            uiState = MemberDetailsUiState.Shown(
                member = UserData(
                    id = "userId",
                    displayName = "John Doe"
                ),
                assignedTasks = listOf(
                    Task(
                        taskId = "task1",
                        title = "Task 1",
                        taskTimestamp = Instant.now().toEpochMilli(),
                        completed = false
                    ),
                    Task(
                        taskId = "task2",
                        title = "Task 2",
                        taskTimestamp = Instant.now().toEpochMilli(),
                        completed = true
                    )
                ),
                showViewAssignedTasksDialog = true,
                selectedTimeRange = TaskTimeRange.Next7Days,
                eventSink = {}
            )
        )
    }
}

@ThemePreview
@Composable
fun SelfDetailsContentPreview() {
    KorenTheme {
        Surface {
            SelfDetailsContent()
        }
    }
}