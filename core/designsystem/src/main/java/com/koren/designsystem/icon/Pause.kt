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

val KorenIcons.Pause: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Pause",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(2f, 6f)
            curveTo(2f, 4.114f, 2f, 3.172f, 2.586f, 2.586f)
            curveTo(3.172f, 2f, 4.114f, 2f, 6f, 2f)
            curveTo(7.886f, 2f, 8.828f, 2f, 9.414f, 2.586f)
            curveTo(10f, 3.172f, 10f, 4.114f, 10f, 6f)
            verticalLineTo(18f)
            curveTo(10f, 19.886f, 10f, 20.828f, 9.414f, 21.414f)
            curveTo(8.828f, 22f, 7.886f, 22f, 6f, 22f)
            curveTo(4.114f, 22f, 3.172f, 22f, 2.586f, 21.414f)
            curveTo(2f, 20.828f, 2f, 19.886f, 2f, 18f)
            verticalLineTo(6f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(14f, 6f)
            curveTo(14f, 4.114f, 14f, 3.172f, 14.586f, 2.586f)
            curveTo(15.172f, 2f, 16.114f, 2f, 18f, 2f)
            curveTo(19.886f, 2f, 20.828f, 2f, 21.414f, 2.586f)
            curveTo(22f, 3.172f, 22f, 4.114f, 22f, 6f)
            verticalLineTo(18f)
            curveTo(22f, 19.886f, 22f, 20.828f, 21.414f, 21.414f)
            curveTo(20.828f, 22f, 19.886f, 22f, 18f, 22f)
            curveTo(16.114f, 22f, 15.172f, 22f, 14.586f, 21.414f)
            curveTo(14f, 20.828f, 14f, 19.886f, 14f, 18f)
            verticalLineTo(6f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun PausePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Pause, contentDescription = null)
    }
}
