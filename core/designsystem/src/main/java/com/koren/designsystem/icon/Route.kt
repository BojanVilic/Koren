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

val KorenIcons.Route: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Route",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 512f,
        viewportHeight = 512f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(416f, 320f)
            horizontalLineToRelative(-96f)
            curveToRelative(-17.6f, 0f, -32f, -14.4f, -32f, -32f)
            reflectiveCurveToRelative(14.4f, -32f, 32f, -32f)
            horizontalLineToRelative(96f)
            reflectiveCurveToRelative(96f, -107f, 96f, -160f)
            reflectiveCurveToRelative(-43f, -96f, -96f, -96f)
            reflectiveCurveToRelative(-96f, 43f, -96f, 96f)
            curveToRelative(0f, 25.5f, 22.2f, 63.4f, 45.3f, 96f)
            lineTo(320f, 192f)
            curveToRelative(-52.9f, 0f, -96f, 43.1f, -96f, 96f)
            reflectiveCurveToRelative(43.1f, 96f, 96f, 96f)
            horizontalLineToRelative(96f)
            curveToRelative(17.6f, 0f, 32f, 14.4f, 32f, 32f)
            reflectiveCurveToRelative(-14.4f, 32f, -32f, 32f)
            lineTo(185.5f, 448f)
            curveToRelative(-16f, 24.8f, -33.8f, 47.7f, -47.3f, 64f)
            lineTo(416f, 512f)
            curveToRelative(52.9f, 0f, 96f, -43.1f, 96f, -96f)
            reflectiveCurveToRelative(-43.1f, -96f, -96f, -96f)
            close()
            moveTo(416f, 64f)
            curveToRelative(17.7f, 0f, 32f, 14.3f, 32f, 32f)
            reflectiveCurveToRelative(-14.3f, 32f, -32f, 32f)
            reflectiveCurveToRelative(-32f, -14.3f, -32f, -32f)
            reflectiveCurveToRelative(14.3f, -32f, 32f, -32f)
            close()
            moveTo(96f, 256f)
            curveToRelative(-53f, 0f, -96f, 43f, -96f, 96f)
            reflectiveCurveToRelative(96f, 160f, 96f, 160f)
            reflectiveCurveToRelative(96f, -107f, 96f, -160f)
            reflectiveCurveToRelative(-43f, -96f, -96f, -96f)
            close()
            moveTo(96f, 384f)
            curveToRelative(-17.7f, 0f, -32f, -14.3f, -32f, -32f)
            reflectiveCurveToRelative(14.3f, -32f, 32f, -32f)
            reflectiveCurveToRelative(32f, 14.3f, 32f, 32f)
            reflectiveCurveToRelative(-14.3f, 32f, -32f, 32f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun RoutePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Route, contentDescription = null)
    }
}
