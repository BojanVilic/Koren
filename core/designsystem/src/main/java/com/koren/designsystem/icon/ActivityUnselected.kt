package com.koren.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val KorenIcons.ActivityUnselected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "ActivityUnselected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(11.077f, 24f)
            curveTo(10.482f, 24f, 10f, 23.552f, 10f, 23f)
            curveTo(10f, 22.448f, 10.482f, 22f, 11.077f, 22f)
            horizontalLineTo(22.923f)
            curveTo(23.518f, 22f, 24f, 22.448f, 24f, 23f)
            curveTo(24f, 23.552f, 23.518f, 24f, 22.923f, 24f)
            horizontalLineTo(11.077f)
            close()
            moveTo(11.077f, 18f)
            curveTo(10.482f, 18f, 10f, 17.552f, 10f, 17f)
            curveTo(10f, 16.448f, 10.482f, 16f, 11.077f, 16f)
            horizontalLineTo(22.923f)
            curveTo(23.518f, 16f, 24f, 16.448f, 24f, 17f)
            curveTo(24f, 17.552f, 23.518f, 18f, 22.923f, 18f)
            horizontalLineTo(11.077f)
            close()
            moveTo(11.077f, 12f)
            curveTo(10.482f, 12f, 10f, 11.552f, 10f, 11f)
            curveTo(10f, 10.448f, 10.482f, 10f, 11.077f, 10f)
            horizontalLineTo(22.923f)
            curveTo(23.518f, 10f, 24f, 10.448f, 24f, 11f)
            curveTo(24f, 11.552f, 23.518f, 12f, 22.923f, 12f)
            horizontalLineTo(11.077f)
            close()
            moveTo(18f, 6f)
            curveTo(18f, 6.552f, 17.552f, 7f, 17f, 7f)
            curveTo(16.448f, 7f, 16f, 6.552f, 16f, 6f)
            verticalLineTo(3f)
            curveTo(16f, 2.448f, 15.552f, 2f, 15f, 2f)
            horizontalLineTo(3f)
            curveTo(2.448f, 2f, 2f, 2.448f, 2f, 3f)
            verticalLineTo(15f)
            curveTo(2f, 15.552f, 2.448f, 16f, 3f, 16f)
            horizontalLineTo(6f)
            curveTo(6.552f, 16f, 7f, 16.448f, 7f, 17f)
            curveTo(7f, 17.552f, 6.552f, 18f, 6f, 18f)
            horizontalLineTo(3f)
            curveTo(1.343f, 18f, 0f, 16.657f, 0f, 15f)
            verticalLineTo(3f)
            curveTo(0f, 1.343f, 1.343f, 0f, 3f, 0f)
            horizontalLineTo(15f)
            curveTo(16.657f, 0f, 18f, 1.343f, 18f, 3f)
            verticalLineTo(6f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun ActivityUnselectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.ActivityUnselected, contentDescription = null)
    }
}
