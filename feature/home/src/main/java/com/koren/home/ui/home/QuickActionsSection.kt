package com.koren.home.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koren.designsystem.components.ActionButton
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.RemovePerson
import com.koren.designsystem.models.ActionItem
import com.koren.designsystem.models.IconResource
import com.koren.home.R

internal fun LazyListScope.quickActionsSection(
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

    item {
        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = "Quick actions",
            style = MaterialTheme.typography.titleSmall
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