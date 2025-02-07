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

val KorenIcons.MapSelected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "MapSelected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.5f,
            strokeAlpha = 0.5f
        ) {
            moveTo(19.716f, 20.362f)
            curveTo(21.143f, 19.585f, 22f, 18.587f, 22f, 17.5f)
            curveTo(22f, 16.347f, 21.037f, 15.296f, 19.454f, 14.5f)
            curveTo(17.623f, 13.579f, 14.962f, 13f, 12f, 13f)
            curveTo(9.038f, 13f, 6.377f, 13.579f, 4.546f, 14.5f)
            curveTo(2.963f, 15.296f, 2f, 16.347f, 2f, 17.5f)
            curveTo(2f, 18.653f, 2.963f, 19.704f, 4.546f, 20.5f)
            curveTo(6.377f, 21.421f, 9.038f, 22f, 12f, 22f)
            curveTo(15.107f, 22f, 17.882f, 21.362f, 19.716f, 20.362f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(5f, 8.515f)
            curveTo(5f, 4.917f, 8.134f, 2f, 12f, 2f)
            curveTo(15.866f, 2f, 19f, 4.917f, 19f, 8.515f)
            curveTo(19f, 12.084f, 16.766f, 16.25f, 13.28f, 17.74f)
            curveTo(12.467f, 18.087f, 11.533f, 18.087f, 10.72f, 17.74f)
            curveTo(7.234f, 16.25f, 5f, 12.084f, 5f, 8.515f)
            close()
            moveTo(12f, 11f)
            curveTo(13.105f, 11f, 14f, 10.105f, 14f, 9f)
            curveTo(14f, 7.895f, 13.105f, 7f, 12f, 7f)
            curveTo(10.895f, 7f, 10f, 7.895f, 10f, 9f)
            curveTo(10f, 10.105f, 10.895f, 11f, 12f, 11f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun MapSelectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.MapSelected, contentDescription = null)
    }
}
