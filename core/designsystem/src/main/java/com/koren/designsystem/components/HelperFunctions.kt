package com.koren.designsystem.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalInspectionMode

fun LazyListState.isEndReached(buffer: Int = 3): Boolean {
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
    return lastVisibleItemIndex >= layoutInfo.totalItemsCount - 1 - buffer
}

@Composable
fun coilPlaceholder(imageVector: ImageVector) =
    if (LocalInspectionMode.current) rememberVectorPainter(imageVector)
    else null