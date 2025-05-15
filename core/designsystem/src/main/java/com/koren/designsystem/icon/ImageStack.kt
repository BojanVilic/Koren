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

val KorenIcons.ImageStack: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "ImageStack",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 576f,
        viewportHeight = 576f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 448f)
            verticalLineToRelative(16f)
            curveToRelative(0f, 26.51f, -21.49f, 48f, -48f, 48f)
            lineTo(48f, 512f)
            curveToRelative(-26.51f, 0f, -48f, -21.49f, -48f, -48f)
            lineTo(0f, 208f)
            curveToRelative(0f, -26.51f, 21.49f, -48f, 48f, -48f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(208f)
            curveToRelative(0f, 44.11f, 35.89f, 80f, 80f, 80f)
            horizontalLineToRelative(336f)
            close()
            moveTo(576f, 368f)
            lineTo(576f, 112f)
            curveToRelative(0f, -26.51f, -21.49f, -48f, -48f, -48f)
            lineTo(144f, 64f)
            curveToRelative(-26.51f, 0f, -48f, 21.49f, -48f, 48f)
            verticalLineToRelative(256f)
            curveToRelative(0f, 26.51f, 21.49f, 48f, 48f, 48f)
            horizontalLineToRelative(384f)
            curveToRelative(26.51f, 0f, 48f, -21.49f, 48f, -48f)
            close()
            moveTo(256f, 160f)
            curveToRelative(0f, 26.51f, -21.49f, 48f, -48f, 48f)
            reflectiveCurveToRelative(-48f, -21.49f, -48f, -48f)
            reflectiveCurveToRelative(21.49f, -48f, 48f, -48f)
            reflectiveCurveToRelative(48f, 21.49f, 48f, 48f)
            close()
            moveTo(160f, 304f)
            lineToRelative(55.51f, -55.51f)
            curveToRelative(4.69f, -4.69f, 12.28f, -4.69f, 16.97f, 0f)
            lineTo(272f, 288f)
            lineToRelative(135.51f, -135.51f)
            curveToRelative(4.69f, -4.69f, 12.28f, -4.69f, 16.97f, 0f)
            lineTo(512f, 240f)
            verticalLineToRelative(112f)
            lineTo(160f, 352f)
            verticalLineToRelative(-48f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun ImageStackPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.ImageStack, contentDescription = null)
    }
}
