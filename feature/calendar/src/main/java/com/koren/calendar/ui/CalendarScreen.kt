package com.koren.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.util.CollectSideEffects
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Serializable
object CalendarDestination

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onShowSnackbar: suspend (message: String) -> Unit
) {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(

        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(
        viewModel = viewModel
    ) { uiSideEffect ->
        when (uiSideEffect) {
            is CalendarUiSideEffect.ShowSnackbar -> onShowSnackbar(uiSideEffect.message)
        }
    }

    CalendarScreenContent(
        uiState = uiState
    )
}

@Composable
private fun CalendarScreenContent(
    uiState: CalendarUiState
) {
    when (uiState) {
        is CalendarUiState.Loading -> CircularProgressIndicator()
        is CalendarUiState.Shown -> CalendarScreenShownContent(uiState = uiState)
    }
}

@Composable
private fun CalendarScreenShownContent(
    uiState: CalendarUiState.Shown
) {
    CalendarUI()
}

@Composable
fun CalendarUI() {
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
            .padding(16.dp)
    ) {
        HeaderSection(currentMonth = displayedMonth.value)
        DaysOfWeekRow()
        MonthPager(pagerState = pagerState, currentMonth = currentMonth)
    }
}


@Composable
fun HeaderSection(currentMonth: YearMonth) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${currentMonth.year}",
                fontSize = 24.sp
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
                    fontSize = 12.sp
                )
            }
        }
    }
    HorizontalDivider()
}


@Composable
fun MonthPager(pagerState: PagerState, currentMonth: YearMonth) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val monthToDisplay = currentMonth.plusMonths((page - (Int.MAX_VALUE / 2)).toLong())
        CalendarGrid(month = monthToDisplay)
    }
}


@Composable
fun CalendarGrid(month: YearMonth) {
    val days = remember(month) { getDaysForMonth(month) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days) { day ->
            DayCell(day = day)
        }
    }
}

data class Day(val dayOfMonth: Int?, val dayOfWeek: DayOfWeek? = null)

fun getDaysForMonth(yearMonth: YearMonth): List<Day> {
    val daysList = mutableListOf<Day>()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek
    val daysInMonth = yearMonth.lengthOfMonth()

    for (i in 1 until firstDayOfWeek.value) {
        daysList.add(Day(dayOfMonth = null))
    }
    for (dayOfMonth in 1..daysInMonth) {
        daysList.add(Day(dayOfMonth = dayOfMonth))
    }

    val totalDays = daysList.size
    val remainingDays = 6 * 7 - totalDays
    for (i in 0 until remainingDays) {
        daysList.add(Day(dayOfMonth = null))
    }
    return daysList
}

@Composable
fun DayCell(day: Day) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (day.dayOfMonth != null) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent
        )
    ) {
        day.dayOfMonth?.let { dayOfMonth ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = dayOfMonth.toString(),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@ThemePreview
@Composable
fun PreviewCalendarUI() {
    KorenTheme {
        CalendarUI()
    }
}

@ThemePreview
@Composable
fun CalendarScreenPreview() {
    KorenTheme {
        CalendarScreenContent(
            uiState = CalendarUiState.Shown(
                eventSink = {}
            )
        )
    }
}