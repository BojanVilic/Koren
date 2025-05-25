package com.koren.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val KorenIcons.Delete: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Delete",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5.755f, 20.283f)
            lineTo(4f, 8f)
            horizontalLineTo(20f)
            lineTo(18.245f, 20.283f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16.265f, 22f)
            horizontalLineTo(7.735f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.755f, 20.283f)
            close()
            moveTo(21f, 4f)
            horizontalLineTo(16f)
            verticalLineTo(3f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, -1f)
            horizontalLineTo(9f)
            arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8f, 3f)
            verticalLineTo(4f)
            horizontalLineTo(3f)
            arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3f, 6f)
            horizontalLineTo(21f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -2f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun DeletePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Delete, contentDescription = null)
    }
}
