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

val KorenIcons.Mic: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Mic",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8f, 5f)
            curveTo(8f, 2.791f, 9.791f, 1f, 12f, 1f)
            curveTo(14.209f, 1f, 16f, 2.791f, 16f, 5f)
            verticalLineTo(12f)
            curveTo(16f, 14.209f, 14.209f, 16f, 12f, 16f)
            curveTo(9.791f, 16f, 8f, 14.209f, 8f, 12f)
            verticalLineTo(5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6.25f, 11.844f)
            verticalLineTo(12f)
            curveTo(6.25f, 13.525f, 6.856f, 14.988f, 7.934f, 16.066f)
            curveTo(9.012f, 17.144f, 10.475f, 17.75f, 12f, 17.75f)
            curveTo(13.525f, 17.75f, 14.988f, 17.144f, 16.066f, 16.066f)
            curveTo(17.144f, 14.988f, 17.75f, 13.525f, 17.75f, 12f)
            verticalLineTo(11.844f)
            curveTo(17.75f, 11.292f, 18.198f, 10.844f, 18.75f, 10.844f)
            horizontalLineTo(19.25f)
            curveTo(19.802f, 10.844f, 20.25f, 11.292f, 20.25f, 11.844f)
            verticalLineTo(12f)
            curveTo(20.25f, 14.188f, 19.381f, 16.287f, 17.834f, 17.834f)
            curveTo(16.584f, 19.083f, 14.975f, 19.89f, 13.25f, 20.155f)
            verticalLineTo(22f)
            curveTo(13.25f, 22.552f, 12.802f, 23f, 12.25f, 23f)
            horizontalLineTo(11.75f)
            curveTo(11.198f, 23f, 10.75f, 22.552f, 10.75f, 22f)
            verticalLineTo(20.155f)
            curveTo(9.025f, 19.89f, 7.416f, 19.083f, 6.166f, 17.834f)
            curveTo(4.619f, 16.287f, 3.75f, 14.188f, 3.75f, 12f)
            verticalLineTo(11.844f)
            curveTo(3.75f, 11.292f, 4.198f, 10.844f, 4.75f, 10.844f)
            horizontalLineTo(5.25f)
            curveTo(5.802f, 10.844f, 6.25f, 11.292f, 6.25f, 11.844f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun MicPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Mic, contentDescription = null)
    }
}
