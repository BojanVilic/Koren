package com.koren.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val KorenIcons.MapUnselected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "MapUnselected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fillAlpha = 0.9f,
            stroke = SolidColor(Color(0xFF000000)),
            strokeAlpha = 0.9f,
            strokeLineWidth = 1.5f
        ) {
            moveTo(5f, 8.515f)
            curveTo(5f, 4.917f, 8.134f, 2f, 12f, 2f)
            curveTo(15.866f, 2f, 19f, 4.917f, 19f, 8.515f)
            curveTo(19f, 12.084f, 16.766f, 16.25f, 13.28f, 17.74f)
            curveTo(12.467f, 18.087f, 11.533f, 18.087f, 10.72f, 17.74f)
            curveTo(7.234f, 16.25f, 5f, 12.084f, 5f, 8.515f)
            close()
        }
        path(
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 1.5f
        ) {
            moveTo(14f, 9f)
            curveTo(14f, 10.105f, 13.105f, 11f, 12f, 11f)
            curveTo(10.895f, 11f, 10f, 10.105f, 10f, 9f)
            curveTo(10f, 7.895f, 10.895f, 7f, 12f, 7f)
            curveTo(13.105f, 7f, 14f, 7.895f, 14f, 9f)
            close()
        }
        path(
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(20.961f, 15.5f)
            curveTo(21.626f, 16.103f, 22f, 16.782f, 22f, 17.5f)
            curveTo(22f, 19.985f, 17.523f, 22f, 12f, 22f)
            curveTo(6.477f, 22f, 2f, 19.985f, 2f, 17.5f)
            curveTo(2f, 16.782f, 2.374f, 16.103f, 3.039f, 15.5f)
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun MapUnselectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.MapUnselected, contentDescription = null)
    }
}
