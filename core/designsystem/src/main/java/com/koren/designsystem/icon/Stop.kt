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

val KorenIcons.Stop: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Stop",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 32f,
        viewportHeight = 32f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5.92f, 24.096f)
            quadToRelative(0f, 0.832f, 0.576f, 1.408f)
            reflectiveQuadToRelative(1.44f, 0.608f)
            horizontalLineToRelative(16.128f)
            quadToRelative(0.832f, 0f, 1.44f, -0.608f)
            reflectiveQuadToRelative(0.576f, -1.408f)
            verticalLineToRelative(-16.16f)
            quadToRelative(0f, -0.832f, -0.576f, -1.44f)
            reflectiveQuadToRelative(-1.44f, -0.576f)
            horizontalLineToRelative(-16.128f)
            quadToRelative(-0.832f, 0f, -1.44f, 0.576f)
            reflectiveQuadToRelative(-0.576f, 1.44f)
            verticalLineToRelative(16.16f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun StopPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Stop, contentDescription = null)
    }
}
