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

val KorenIcons.ActivitySelected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "ActivitySelected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(10.077f, 22.5f)
            curveTo(10.077f, 21.672f, 10.748f, 21f, 11.577f, 21f)
            horizontalLineTo(22.423f)
            curveTo(23.251f, 21f, 23.923f, 21.672f, 23.923f, 22.5f)
            curveTo(23.923f, 23.328f, 23.251f, 24f, 22.423f, 24f)
            horizontalLineTo(11.577f)
            curveTo(10.748f, 24f, 10.077f, 23.328f, 10.077f, 22.5f)
            close()
            moveTo(10.077f, 17f)
            curveTo(10.077f, 16.172f, 10.748f, 15.5f, 11.577f, 15.5f)
            horizontalLineTo(22.423f)
            curveTo(23.251f, 15.5f, 23.923f, 16.172f, 23.923f, 17f)
            curveTo(23.923f, 17.828f, 23.251f, 18.5f, 22.423f, 18.5f)
            horizontalLineTo(11.577f)
            curveTo(10.748f, 18.5f, 10.077f, 17.828f, 10.077f, 17f)
            close()
            moveTo(10.077f, 11.5f)
            curveTo(10.077f, 10.672f, 10.748f, 10f, 11.577f, 10f)
            horizontalLineTo(22.423f)
            curveTo(23.251f, 10f, 23.923f, 10.672f, 23.923f, 11.5f)
            curveTo(23.923f, 12.328f, 23.251f, 13f, 22.423f, 13f)
            horizontalLineTo(11.577f)
            curveTo(10.748f, 13f, 10.077f, 12.328f, 10.077f, 11.5f)
            close()
            moveTo(18f, 7f)
            horizontalLineTo(10f)
            curveTo(8.343f, 7f, 7f, 8.343f, 7f, 10f)
            verticalLineTo(18f)
            horizontalLineTo(3f)
            curveTo(1.343f, 18f, 0f, 16.657f, 0f, 15f)
            verticalLineTo(3f)
            curveTo(0f, 1.343f, 1.343f, 0f, 3f, 0f)
            horizontalLineTo(15f)
            curveTo(16.657f, 0f, 18f, 1.343f, 18f, 3f)
            verticalLineTo(7f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun ActivitySelectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.ActivitySelected, contentDescription = null)
    }
}
