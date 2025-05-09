package com.koren.designsystem.components

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isEndReached(buffer: Int = 3): Boolean {
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
    return lastVisibleItemIndex >= layoutInfo.totalItemsCount - 1 - buffer
}