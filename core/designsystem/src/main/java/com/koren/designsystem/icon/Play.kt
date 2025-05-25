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

val KorenIcons.Play: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Play",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(21.409f, 9.353f)
            curveTo(23.531f, 10.507f, 23.531f, 13.493f, 21.409f, 14.647f)
            lineTo(8.597f, 21.615f)
            curveTo(6.534f, 22.736f, 4f, 21.276f, 4f, 18.967f)
            lineTo(4f, 5.033f)
            curveTo(4f, 2.724f, 6.534f, 1.264f, 8.597f, 2.385f)
            lineTo(21.409f, 9.353f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun PlayPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Play, contentDescription = null)
    }
}
