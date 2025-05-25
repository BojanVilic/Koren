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

val KorenIcons.Restart: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Restart",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            fillAlpha = 0.5f,
            strokeAlpha = 0.5f,
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(6.873f, 7.873f)
            curveTo(9.016f, 5.731f, 12.167f, 5.209f, 14.801f, 6.31f)
            lineTo(15.931f, 5.18f)
            curveTo(12.651f, 3.531f, 8.551f, 4.074f, 5.813f, 6.813f)
            curveTo(2.396f, 10.23f, 2.396f, 15.77f, 5.813f, 19.187f)
            curveTo(9.23f, 22.604f, 14.77f, 22.604f, 18.187f, 19.187f)
            curveTo(20.175f, 17.2f, 21.006f, 14.493f, 20.682f, 11.907f)
            curveTo(20.63f, 11.496f, 20.256f, 11.205f, 19.844f, 11.256f)
            curveTo(19.434f, 11.308f, 19.142f, 11.683f, 19.194f, 12.094f)
            curveTo(19.462f, 14.24f, 18.773f, 16.48f, 17.126f, 18.126f)
            curveTo(14.295f, 20.958f, 9.705f, 20.958f, 6.873f, 18.126f)
            curveTo(4.042f, 15.295f, 4.042f, 10.705f, 6.873f, 7.873f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(18.721f, 4.201f)
            curveTo(18.721f, 3.898f, 18.538f, 3.624f, 18.258f, 3.508f)
            curveTo(17.978f, 3.392f, 17.655f, 3.456f, 17.441f, 3.671f)
            lineTo(15.931f, 5.18f)
            lineTo(14.801f, 6.311f)
            lineTo(13.198f, 7.913f)
            curveTo(12.984f, 8.128f, 12.92f, 8.451f, 13.036f, 8.731f)
            curveTo(13.152f, 9.011f, 13.425f, 9.194f, 13.729f, 9.194f)
            horizontalLineTo(17.971f)
            curveTo(18.385f, 9.194f, 18.721f, 8.858f, 18.721f, 8.444f)
            verticalLineTo(4.201f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun RestartPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Restart, contentDescription = null)
    }
}
