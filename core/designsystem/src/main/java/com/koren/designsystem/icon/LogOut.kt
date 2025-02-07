package com.koren.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val KorenIcons.LogOut: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "LogOut",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14f, 20f)
            horizontalLineTo(6f)
            curveTo(4.895f, 20f, 4f, 19.105f, 4f, 18f)
            lineTo(4f, 6f)
            curveTo(4f, 4.895f, 4.895f, 4f, 6f, 4f)
            horizontalLineTo(14f)
            moveTo(10f, 12f)
            horizontalLineTo(21f)
            moveTo(21f, 12f)
            lineTo(18f, 15f)
            moveTo(21f, 12f)
            lineTo(18f, 9f)
        }
    }.build()
}
