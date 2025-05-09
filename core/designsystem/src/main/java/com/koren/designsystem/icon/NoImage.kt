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

val KorenIcons.NoImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "NoImage",
        defaultWidth = 64.dp,
        defaultHeight = 64.dp,
        viewportWidth = 32f,
        viewportHeight = 32f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(30f, 3.414f)
            lineTo(28.586f, 2f)
            lineTo(2f, 28.586f)
            lineTo(3.414f, 30f)
            lineToRelative(2f, -2f)
            lineTo(26f, 28f)
            arcToRelative(2.003f, 2.003f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, -2f)
            lineTo(28f, 5.414f)
            close()
            moveTo(26f, 26f)
            lineTo(7.414f, 26f)
            lineToRelative(7.793f, -7.793f)
            lineToRelative(2.379f, 2.379f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.828f, 0f)
            lineTo(22f, 19f)
            lineToRelative(4f, 3.997f)
            close()
            moveTo(26f, 20.168f)
            lineTo(23.414f, 17.582f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.828f, 0f)
            lineTo(19f, 19.168f)
            lineToRelative(-2.377f, -2.377f)
            lineTo(26f, 7.414f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6f, 22f)
            verticalLineTo(19f)
            lineToRelative(5f, -4.997f)
            lineToRelative(1.373f, 1.373f)
            lineToRelative(1.416f, -1.416f)
            lineToRelative(-1.375f, -1.375f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.828f, 0f)
            lineTo(6f, 16.172f)
            verticalLineTo(6f)
            horizontalLineTo(22f)
            verticalLineTo(4f)
            horizontalLineTo(6f)
            arcTo(2.002f, 2.002f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 6f)
            verticalLineTo(22f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun NoImagePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.NoImage, contentDescription = null)
    }
}
