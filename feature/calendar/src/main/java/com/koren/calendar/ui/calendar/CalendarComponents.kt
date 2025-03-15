package com.koren.calendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koren.calendar.ui.Day
import com.koren.common.models.calendar.Event
import com.koren.common.models.calendar.Task
import com.koren.common.util.toLocalDate
import com.koren.designsystem.theme.ExtendedTheme
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarUI(
    uiState: CalendarUiState.Shown
) {
    val currentMonth = remember { YearMonth.now() }
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val displayedMonth = remember { mutableStateOf(currentMonth) }

    LaunchedEffect(pagerState.currentPage) {
        displayedMonth.value = currentMonth.plusMonths((pagerState.currentPage - (Int.MAX_VALUE / 2)).toLong())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderSection(currentMonth = displayedMonth.value)
            DaysOfWeekRow()
        }
        MonthPager(
            pagerState = pagerState,
            currentMonth = currentMonth,
            groupedTasks = uiState.groupedTasks,
            groupedEvents = uiState.groupedEvents,
            dayClicked =  { day ->
                uiState.eventSink(CalendarUiEvent.DayClicked(day))
            }
        )
    }
}


@Composable
fun HeaderSection(currentMonth: YearMonth) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ExtendedTheme.colors.event)
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Event",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ExtendedTheme.colors.task)
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Task",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DaysOfWeekRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = DayOfWeek.entries.toTypedArray()
        daysOfWeek.forEach { dayOfWeek ->
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
    HorizontalDivider()
}


@Composable
fun MonthPager(
    pagerState: PagerState,
    currentMonth: YearMonth,
    groupedTasks: Map<LocalDate, List<Task>>,
    groupedEvents: Map<LocalDate, List<Event>>,
    dayClicked: (Day) -> Unit
) {
    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState
    ) { page ->
        val monthToDisplay = currentMonth.plusMonths((page - (Int.MAX_VALUE / 2)).toLong())
        CalendarGrid(
            month = monthToDisplay,
            groupedTasks = groupedTasks,
            groupedEvents = groupedEvents,
            dayClicked = dayClicked
        )
    }
}


@Composable
fun CalendarGrid(
    month: YearMonth,
    groupedTasks: Map<LocalDate, List<Task>>,
    groupedEvents: Map<LocalDate, List<Event>>,
    dayClicked: (Day) -> Unit
) {
    val days = remember(month) { getDaysForMonth(month, groupedTasks, groupedEvents) }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        columns = GridCells.Fixed(7)
    ) {
        items(days) { day ->
            DayCell(
                day = day,
                dayClicked = dayClicked
            )
        }
    }
}

fun getDaysForMonth(
    yearMonth: YearMonth,
    groupedTasks: Map<LocalDate, List<Task>>,
    groupedEvents: Map<LocalDate, List<Event>>
): List<Day> {
    val daysList = mutableListOf<Day>()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek
    val daysInMonth = yearMonth.lengthOfMonth()

    // Add empty days for the leading days of the week
    for (i in 1 until firstDayOfWeek.value) {
        daysList.add(Day())
    }

    for (dayOfMonth in 1..daysInMonth) {
        val currentDate = yearMonth.atDay(dayOfMonth)
        val tasksForDay = groupedTasks[currentDate] ?: emptyList()
        val eventsForDay = groupedEvents[currentDate] ?: emptyList()

        daysList.add(
            Day(
                dayOfMonth = dayOfMonth,
                dayOfWeek = currentDate.dayOfWeek,
                localDate = currentDate,
                tasks = tasksForDay,
                events = eventsForDay
            )
        )
    }

    // Add empty days for the trailing days of the week
    val totalDays = daysList.size
    val remainingDays = 6 * 7 - totalDays
    for (i in 0 until remainingDays) {
        daysList.add(Day())
    }
    return daysList
}

private fun isEventOnDate(
    event: Event,
    date: LocalDate
): Boolean {
    val eventStartDate = event.eventStartTime.toLocalDate()
    val eventEndDate = event.eventEndTime.toLocalDate()
    return if (event.isAllDay) {
        !date.isBefore(eventStartDate) && !date.isAfter(eventEndDate)
    } else {
        val eventStartTimeLocalDate = event.eventStartTime.toLocalDate()
        val eventEndTimeLocalDate = event.eventEndTime.toLocalDate()
        !date.isBefore(eventStartTimeLocalDate) && !date.isAfter(eventEndTimeLocalDate)
    }
}

@Composable
fun DayCell(
    day: Day,
    dayClicked: (Day) -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (day.dayOfMonth != null)
                    MaterialTheme.colorScheme.surfaceContainerHighest
                else
                    Color.Transparent
        )
    ) {
        day.dayOfMonth?.let { dayOfMonth ->

            if (day.events.isNotEmpty() || day.tasks.isNotEmpty()) {
                Timber.d("PROBAVANJE: $dayOfMonth, events: ${day.events.size}, tasks: ${day.tasks.size}")
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { dayClicked(day) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (day.events.isNotEmpty() || day.tasks.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                        ) {
                            if (day.events.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ExtendedTheme.colors.event)
                                )
                            }

                            if (day.events.isNotEmpty() && day.tasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(2.dp))
                            }

                            if (day.tasks.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ExtendedTheme.colors.task)
                                )
                            }
                        }
                    }

                    Text(
                        text = dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@ThemePreview
@Composable
fun CalendarUIPreview() {
    KorenTheme {
        CalendarUI(
            uiState = CalendarUiState.Shown(
                eventSink = {}
            )
        )
    }
}