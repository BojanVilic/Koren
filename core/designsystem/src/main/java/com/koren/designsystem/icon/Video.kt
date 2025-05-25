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

val KorenIcons.Video: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Video",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(15.328f, 7.542f)
            horizontalLineTo(8.672f)
            curveTo(5.298f, 7.542f, 3.61f, 7.542f, 2.662f, 8.529f)
            curveTo(1.714f, 9.516f, 1.937f, 11.04f, 2.384f, 14.09f)
            lineTo(2.806f, 16.981f)
            curveTo(3.156f, 19.372f, 3.331f, 20.568f, 4.228f, 21.284f)
            curveTo(5.126f, 22f, 6.449f, 22f, 9.095f, 22f)
            horizontalLineTo(14.905f)
            curveTo(17.551f, 22f, 18.875f, 22f, 19.772f, 21.284f)
            curveTo(20.669f, 20.568f, 20.844f, 19.372f, 21.194f, 16.981f)
            lineTo(21.617f, 14.09f)
            curveTo(22.063f, 11.04f, 22.285f, 9.516f, 21.338f, 8.529f)
            curveTo(20.39f, 7.542f, 18.702f, 7.542f, 15.328f, 7.542f)
            close()
            moveTo(14.581f, 15.794f)
            curveTo(15.14f, 15.448f, 15.14f, 14.552f, 14.581f, 14.206f)
            lineTo(11.21f, 12.116f)
            curveTo(10.667f, 11.779f, 10f, 12.217f, 10f, 12.91f)
            verticalLineTo(17.09f)
            curveTo(10f, 17.783f, 10.667f, 18.221f, 11.21f, 17.884f)
            lineTo(14.581f, 15.794f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            fillAlpha = 0.4f,
            strokeAlpha = 0.4f
        ) {
            moveTo(8.51f, 2f)
            horizontalLineTo(15.49f)
            curveTo(15.722f, 2f, 15.9f, 2f, 16.056f, 2.015f)
            curveTo(17.164f, 2.124f, 18.071f, 2.79f, 18.455f, 3.687f)
            horizontalLineTo(5.544f)
            curveTo(5.928f, 2.79f, 6.835f, 2.124f, 7.943f, 2.015f)
            curveTo(8.099f, 2f, 8.277f, 2f, 8.51f, 2f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            fillAlpha = 0.7f,
            strokeAlpha = 0.7f
        ) {
            moveTo(6.31f, 4.723f)
            curveTo(4.92f, 4.723f, 3.779f, 5.562f, 3.399f, 6.676f)
            curveTo(3.391f, 6.7f, 3.383f, 6.723f, 3.376f, 6.746f)
            curveTo(3.774f, 6.626f, 4.188f, 6.547f, 4.608f, 6.493f)
            curveTo(5.688f, 6.355f, 7.054f, 6.355f, 8.64f, 6.355f)
            horizontalLineTo(15.532f)
            curveTo(17.118f, 6.355f, 18.483f, 6.355f, 19.563f, 6.493f)
            curveTo(19.983f, 6.547f, 20.397f, 6.626f, 20.795f, 6.746f)
            curveTo(20.788f, 6.723f, 20.781f, 6.7f, 20.773f, 6.676f)
            curveTo(20.392f, 5.562f, 19.252f, 4.723f, 17.861f, 4.723f)
            horizontalLineTo(6.31f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun VideoPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Video, contentDescription = null)
    }
}
