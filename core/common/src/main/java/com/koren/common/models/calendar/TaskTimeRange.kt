package com.koren.common.models.calendar

sealed interface TaskTimeRange {
    data object Next24Hours : TaskTimeRange
    data object Next7Days : TaskTimeRange
    data object Next14Days : TaskTimeRange
    data object Next30Days : TaskTimeRange
}