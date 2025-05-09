package com.koren.activity.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.activity.R
import com.koren.common.models.activity.UserLocationActivity
import com.koren.common.models.user.UserData
import com.koren.common.util.CollectSideEffects
import com.koren.common.util.DateUtils.toRelativeTime
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.components.isEndReached
import com.koren.designsystem.icon.Event
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
object ActivityDestination

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = hiltViewModel(),
    navigateToCalendar: () -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            isTopBarVisible = false,
            isBottomBarVisible = true
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { sideEffect ->
        when (sideEffect) {
            is ActivitySideEffect.NavigateToCalendar -> navigateToCalendar()
        }
    }

    ActivityScreenContent(uiState = uiState)
}

@Composable
private fun ActivityScreenContent(
    uiState: ActivityUiState
) {
    when (uiState) {
        is ActivityUiState.NoFamily -> NoFamilyContent()
        is ActivityUiState.Error -> Unit
        is ActivityUiState.Loading -> LoadingContent()
        is ActivityUiState.Shown -> ShownContent(uiState = uiState)
    }
}

@Composable
private fun NoFamilyContent() {
    Text(text = "No family")
}

@Composable
fun ShownContent(uiState: ActivityUiState.Shown) {
    val groupedActivities = groupActivitiesByDay(uiState.activities)
    val listState = rememberLazyListState()

    val endOfListReached by remember {
        derivedStateOf {
            listState.isEndReached()
        }
    }

    LaunchedEffect(endOfListReached, uiState.fetchingMore, uiState.canFetchMore) {
        if (endOfListReached && !uiState.fetchingMore && uiState.canFetchMore) {
            uiState.eventSink(ActivityEvent.FetchMoreActivities)
        }
    }

    Column {

        Card(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            onClick = { uiState.eventSink(ActivityEvent.NavigateToCalendar) },
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = KorenIcons.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Calendar",
                )
            }
        }

        LazyColumn(
            state = listState
        ) {
            groupedActivities.forEach { (date, activities) ->
                item {
                    DateHeader(date)
                }
                items(activities) { activity ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        LocationActivityListItem(userLocationActivity = activity)
                    }
                }
            }
            if (uiState.fetchingMore) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        text = stringResource(com.koren.designsystem.R.string.loading),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun groupActivitiesByDay(activities: List<UserLocationActivity>): Map<String, List<UserLocationActivity>> {
    val grouped = mutableMapOf<String, MutableList<UserLocationActivity>>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    for (activity in activities) {
        val date = dateFormat.format(Date(activity.createdAt))
        grouped.getOrPut(date) { mutableListOf() }.add(activity)
    }
    return grouped
}


@Composable
fun DateHeader(date: String) {
    val dateLong = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.time ?: 0
    val relativeDate = DateUtils.getRelativeTimeSpanString(
        dateLong,
        System.currentTimeMillis(),
        DateUtils.DAY_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_ALL
    ).toString()
    Text(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = relativeDate
    )
}

@Composable
private fun LocationActivityListItem(
    userLocationActivity: UserLocationActivity
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
                if (userLocationActivity.inTransit) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.transit),
                        contentDescription = "Location"
                    )

                    Text(
                        modifier = Modifier
                            .padding(6.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onPrimary,
                                MaterialTheme.shapes.medium
                            )
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
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onPrimary,
                                MaterialTheme.shapes.medium
                            )
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
                    text = userLocationActivity.createdAt.toRelativeTime(),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(userLocationActivity.userData?.profilePictureUrl)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "${userLocationActivity.userData?.displayName} is at ${userLocationActivity.locationName}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
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
                    UserLocationActivity(
                        userData = UserData(displayName = "John Doe"),
                        locationName = "Home",
                        inTransit = false,
                        createdAt = 192837465156
                    ),
                    UserLocationActivity(
                        userData = UserData(displayName = "Jane Doe"),
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