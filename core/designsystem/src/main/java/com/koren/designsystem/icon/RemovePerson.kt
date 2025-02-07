package com.koren.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val KorenIcons.RemovePerson: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "RemovePerson",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8.5f, 8.5f)
            curveTo(8.5f, 6.566f, 10.066f, 5f, 12f, 5f)
            curveTo(13.934f, 5f, 15.5f, 6.566f, 15.5f, 8.5f)
            curveTo(15.5f, 10.434f, 13.934f, 12f, 12f, 12f)
            curveTo(10.066f, 12f, 8.5f, 10.434f, 8.5f, 8.5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 13.75f)
            curveTo(9.664f, 13.75f, 5f, 14.922f, 5f, 17.25f)
            verticalLineTo(19f)
            horizontalLineTo(19f)
            verticalLineTo(17.25f)
            curveTo(19f, 14.922f, 14.336f, 13.75f, 12f, 13.75f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(21f, 11f)
            horizontalLineTo(17f)
            verticalLineTo(13f)
            horizontalLineTo(21f)
            verticalLineTo(11f)
            close()
        }
    }.build()
}
