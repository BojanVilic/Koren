package com.koren.activity.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector
import com.koren.designsystem.icon.Event
import com.koren.designsystem.icon.KorenIcons
import okhttp3.internal.immutableListOf

data class ActivityActionItem(
    val text: String,
    val icon: ImageVector,
    val event: ActivityEvent
)

val actionItems = immutableListOf(
    ActivityActionItem(
        icon = KorenIcons.Event,
        text = "Calendar",
        event = ActivityEvent.NavigateToCalendar
    ),
    ActivityActionItem(
        icon = Icons.Default.QuestionMark,
        text = "Answers",
        event = ActivityEvent.AnswersClicked
    ),
    ActivityActionItem(
        icon = Icons.Default.Emergency,
        text = "Emergency",
        event = ActivityEvent.AnswersClicked
    )
)