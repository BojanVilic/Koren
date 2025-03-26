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

val KorenIcons.CallHome: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "CallHome",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF020202)),
            strokeLineWidth = 1.87f
        ) {
            moveTo(9.22f, 21.33f)
            reflectiveCurveTo(1.75f, 14.8f, 1.75f, 9.2f)
            arcToRelative(7.47f, 7.47f, 0f, isMoreThanHalf = true, isPositiveArc = true, 14.93f, 0f)
        }
        path(
            stroke = SolidColor(Color(0xFF020202)),
            strokeLineWidth = 1.87f
        ) {
            moveTo(20.42f, 15.73f)
            lineToRelative(0f, 6.54f)
            lineToRelative(-9.34f, 0f)
            lineToRelative(0f, -6.54f)
        }
        path(
            stroke = SolidColor(Color(0xFF020202)),
            strokeLineWidth = 1.87f
        ) {
            moveTo(15.75f, 18.53f)
            lineTo(15.75f, 22.27f)
        }
        path(
            stroke = SolidColor(Color(0xFF020202)),
            strokeLineWidth = 1.87f
        ) {
            moveTo(9.22f, 9.2f)
            moveToRelative(-2.8f, 0f)
            arcToRelative(2.8f, 2.8f, 0f, isMoreThanHalf = true, isPositiveArc = true, 5.6f, 0f)
            arcToRelative(2.8f, 2.8f, 0f, isMoreThanHalf = true, isPositiveArc = true, -5.6f, 0f)
        }
        path(
            stroke = SolidColor(Color(0xFF020202)),
            strokeLineWidth = 1.87f
        ) {
            moveTo(8.75f, 18.07f)
            lineToRelative(7f, -7f)
            lineToRelative(7f, 7f)
        }
    }.build()
}

@Preview
@Composable
private fun CallHomePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.CallHome, contentDescription = null)
    }
}
