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

val KorenIcons.Close: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Close",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(22f, 12f)
            curveTo(22f, 17.523f, 17.523f, 22f, 12f, 22f)
            curveTo(6.477f, 22f, 2f, 17.523f, 2f, 12f)
            curveTo(2f, 6.477f, 6.477f, 2f, 12f, 2f)
            curveTo(17.523f, 2f, 22f, 6.477f, 22f, 12f)
            close()
            moveTo(8.97f, 8.97f)
            curveTo(9.263f, 8.677f, 9.737f, 8.677f, 10.03f, 8.97f)
            lineTo(12f, 10.939f)
            lineTo(13.97f, 8.97f)
            curveTo(14.262f, 8.677f, 14.737f, 8.677f, 15.03f, 8.97f)
            curveTo(15.323f, 9.263f, 15.323f, 9.737f, 15.03f, 10.03f)
            lineTo(13.061f, 12f)
            lineTo(15.03f, 13.97f)
            curveTo(15.323f, 14.262f, 15.323f, 14.737f, 15.03f, 15.03f)
            curveTo(14.737f, 15.323f, 14.262f, 15.323f, 13.97f, 15.03f)
            lineTo(12f, 13.061f)
            lineTo(10.03f, 15.03f)
            curveTo(9.737f, 15.323f, 9.263f, 15.323f, 8.97f, 15.03f)
            curveTo(8.677f, 14.737f, 8.677f, 14.262f, 8.97f, 13.97f)
            lineTo(10.939f, 12f)
            lineTo(8.97f, 10.03f)
            curveTo(8.677f, 9.737f, 8.677f, 9.263f, 8.97f, 8.97f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun ClosePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Close, contentDescription = null)
    }
}
