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

val KorenIcons.Copy: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Copy",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(15.24f, 2f)
            horizontalLineTo(11.346f)
            curveTo(9.582f, 2f, 8.184f, 2f, 7.091f, 2.148f)
            curveTo(5.965f, 2.3f, 5.054f, 2.62f, 4.336f, 3.341f)
            curveTo(3.617f, 4.062f, 3.298f, 4.977f, 3.147f, 6.107f)
            curveTo(3f, 7.205f, 3f, 8.608f, 3f, 10.379f)
            verticalLineTo(16.217f)
            curveTo(3f, 17.725f, 3.92f, 19.017f, 5.227f, 19.559f)
            curveTo(5.16f, 18.65f, 5.16f, 17.374f, 5.16f, 16.312f)
            lineTo(5.16f, 11.398f)
            lineTo(5.16f, 11.302f)
            curveTo(5.16f, 10.021f, 5.16f, 8.916f, 5.278f, 8.032f)
            curveTo(5.405f, 7.084f, 5.691f, 6.176f, 6.425f, 5.439f)
            curveTo(7.159f, 4.702f, 8.064f, 4.415f, 9.008f, 4.287f)
            curveTo(9.889f, 4.169f, 10.989f, 4.169f, 12.265f, 4.169f)
            lineTo(12.36f, 4.169f)
            horizontalLineTo(15.24f)
            lineTo(15.335f, 4.169f)
            curveTo(16.611f, 4.169f, 17.709f, 4.169f, 18.59f, 4.287f)
            curveTo(18.063f, 2.948f, 16.762f, 2f, 15.24f, 2f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(6.6f, 11.397f)
            curveTo(6.6f, 8.671f, 6.6f, 7.308f, 7.444f, 6.461f)
            curveTo(8.287f, 5.614f, 9.645f, 5.614f, 12.36f, 5.614f)
            horizontalLineTo(15.24f)
            curveTo(17.955f, 5.614f, 19.313f, 5.614f, 20.157f, 6.461f)
            curveTo(21f, 7.308f, 21f, 8.671f, 21f, 11.397f)
            verticalLineTo(16.217f)
            curveTo(21f, 18.943f, 21f, 20.306f, 20.157f, 21.153f)
            curveTo(19.313f, 22f, 17.955f, 22f, 15.24f, 22f)
            horizontalLineTo(12.36f)
            curveTo(9.645f, 22f, 8.287f, 22f, 7.444f, 21.153f)
            curveTo(6.6f, 20.306f, 6.6f, 18.943f, 6.6f, 16.217f)
            verticalLineTo(11.397f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun CopyPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Copy, contentDescription = null)
    }
}
