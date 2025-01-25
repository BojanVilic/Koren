package com.koren.activity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.activity.R
import com.koren.common.models.activity.LocationActivity
import com.koren.common.models.invitation.InvitationStatus
import com.koren.common.models.invitation.getExpiryText
import com.koren.common.models.invitation.toHumanReadableDateTime
import com.koren.common.models.invitation.toRelativeTime
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable
import org.w3c.dom.Text

@Serializable
object ActivityDestination

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = hiltViewModel()
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = false,
            isBottomBarVisible = true
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActivityScreenContent(uiState = uiState)
}

@Composable
private fun ActivityScreenContent(
    uiState: ActivityUiState
) {
    when (uiState) {
        is ActivityUiState.Empty -> Unit
        is ActivityUiState.Error -> Unit
        is ActivityUiState.Loading -> LoadingContent()
        is ActivityUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
fun ShownContent(uiState: ActivityUiState.Shown) {
    Column {
        LazyColumn {
            items(uiState.activities) { activity ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    LocationActivityListItem(locationActivity = activity)
                }
            }
        }
    }
}

@Composable
private fun LocationActivityListItem(
    locationActivity: LocationActivity
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {

                }
            ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (locationActivity.inTransit) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.transit),
                        contentDescription = "Location"
                    )

                    Text(
                        modifier = Modifier
                            .padding(6.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp),
                        text = "In Transit",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                else {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location"
                    )
                    Text(
                        modifier = Modifier
                            .padding(6.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.medium)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp),
                        text = "At Location",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = locationActivity.createdAt.toRelativeTime(),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                modifier = Modifier.padding(16.dp),
                text = "${locationActivity.userDisplayName} is at ${locationActivity.locationName}",
                style = MaterialTheme.typography.labelLarge
            )
    }
        }
}

@ThemePreview
@Composable
fun ActivityScreenPreview() {
    KorenTheme {
        ActivityScreenContent(
            uiState = ActivityUiState.Shown(
                activities = listOf(
                    LocationActivity(
                        userDisplayName = "John Doe",
                        locationName = "Home",
                        inTransit = false,
                        createdAt = 192837465156
                    ),
                    LocationActivity(
                        userDisplayName = "Jane Doe",
                        locationName = "Work",
                        inTransit = true,
                        createdAt = 192837465156
                    )
                ),
                eventSink = {}
            )
        )
    }
}