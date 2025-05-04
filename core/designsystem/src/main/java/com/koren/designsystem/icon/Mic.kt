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

val KorenIcons.Mic: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Mic",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(4f, 9f)
            curveTo(4.414f, 9f, 4.75f, 9.336f, 4.75f, 9.75f)
            verticalLineTo(10.75f)
            curveTo(4.75f, 14.754f, 7.996f, 18f, 12f, 18f)
            curveTo(16.004f, 18f, 19.25f, 14.754f, 19.25f, 10.75f)
            verticalLineTo(9.75f)
            curveTo(19.25f, 9.336f, 19.586f, 9f, 20f, 9f)
            curveTo(20.414f, 9f, 20.75f, 9.336f, 20.75f, 9.75f)
            verticalLineTo(10.75f)
            curveTo(20.75f, 15.33f, 17.231f, 19.088f, 12.75f, 19.468f)
            verticalLineTo(21.75f)
            curveTo(12.75f, 22.164f, 12.414f, 22.5f, 12f, 22.5f)
            curveTo(11.586f, 22.5f, 11.25f, 22.164f, 11.25f, 21.75f)
            verticalLineTo(19.468f)
            curveTo(6.769f, 19.088f, 3.25f, 15.33f, 3.25f, 10.75f)
            verticalLineTo(9.75f)
            curveTo(3.25f, 9.336f, 3.586f, 9f, 4f, 9f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            fillAlpha = 0.5f,
            strokeAlpha = 0.5f
        ) {
            moveTo(9.75f, 7.75f)
            curveTo(9.75f, 7.336f, 9.414f, 7f, 9f, 7f)
            horizontalLineTo(7.816f)
            horizontalLineTo(6.298f)
            curveTo(6.666f, 4.179f, 9.079f, 2f, 12f, 2f)
            curveTo(14.921f, 2f, 17.334f, 4.179f, 17.701f, 7f)
            horizontalLineTo(16.184f)
            lineTo(13.5f, 7f)
            curveTo(13.086f, 7f, 12.75f, 7.336f, 12.75f, 7.75f)
            curveTo(12.75f, 8.164f, 13.086f, 8.5f, 13.5f, 8.5f)
            lineTo(16.25f, 8.5f)
            horizontalLineTo(17.75f)
            verticalLineTo(10f)
            horizontalLineTo(16.25f)
            horizontalLineTo(13.5f)
            curveTo(13.086f, 10f, 12.75f, 10.336f, 12.75f, 10.75f)
            curveTo(12.75f, 11.164f, 13.086f, 11.5f, 13.5f, 11.5f)
            horizontalLineTo(16.184f)
            horizontalLineTo(17.701f)
            curveTo(17.334f, 14.321f, 14.921f, 16.5f, 12f, 16.5f)
            curveTo(9.079f, 16.5f, 6.666f, 14.321f, 6.298f, 11.5f)
            horizontalLineTo(7.816f)
            horizontalLineTo(9f)
            curveTo(9.414f, 11.5f, 9.75f, 11.164f, 9.75f, 10.75f)
            curveTo(9.75f, 10.336f, 9.414f, 10f, 9f, 10f)
            horizontalLineTo(7.75f)
            horizontalLineTo(6.25f)
            verticalLineTo(8.5f)
            horizontalLineTo(7.75f)
            horizontalLineTo(9f)
            curveTo(9.414f, 8.5f, 9.75f, 8.164f, 9.75f, 7.75f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(12.75f, 10.75f)
            curveTo(12.75f, 11.164f, 13.086f, 11.5f, 13.5f, 11.5f)
            horizontalLineTo(16.184f)
            horizontalLineTo(17.701f)
            lineTo(17.75f, 10f)
            horizontalLineTo(16.25f)
            horizontalLineTo(13.5f)
            curveTo(13.086f, 10f, 12.75f, 10.336f, 12.75f, 10.75f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(12.75f, 7.75f)
            curveTo(12.75f, 8.164f, 13.086f, 8.5f, 13.5f, 8.5f)
            lineTo(16.25f, 8.5f)
            horizontalLineTo(17.75f)
            lineTo(17.701f, 7f)
            horizontalLineTo(16.184f)
            lineTo(13.5f, 7f)
            curveTo(13.086f, 7f, 12.75f, 7.336f, 12.75f, 7.75f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(9.75f, 7.75f)
            curveTo(9.75f, 7.336f, 9.414f, 7f, 9f, 7f)
            horizontalLineTo(7.816f)
            horizontalLineTo(6.298f)
            lineTo(6.25f, 8.5f)
            horizontalLineTo(7.75f)
            horizontalLineTo(9f)
            curveTo(9.414f, 8.5f, 9.75f, 8.164f, 9.75f, 7.75f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(9.75f, 10.75f)
            curveTo(9.75f, 10.336f, 9.414f, 10f, 9f, 10f)
            horizontalLineTo(7.75f)
            horizontalLineTo(6.25f)
            lineTo(6.298f, 11.5f)
            horizontalLineTo(7.816f)
            horizontalLineTo(9f)
            curveTo(9.414f, 11.5f, 9.75f, 11.164f, 9.75f, 10.75f)
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
