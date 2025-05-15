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

val KorenIcons.Files: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Files",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(8.51f, 2f)
            horizontalLineTo(15.49f)
            curveTo(15.722f, 2f, 15.901f, 2f, 16.056f, 2.015f)
            curveTo(17.164f, 2.124f, 18.071f, 2.79f, 18.456f, 3.687f)
            horizontalLineTo(5.544f)
            curveTo(5.929f, 2.79f, 6.836f, 2.124f, 7.943f, 2.015f)
            curveTo(8.099f, 2f, 8.277f, 2f, 8.51f, 2f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(6.311f, 4.723f)
            curveTo(4.92f, 4.723f, 3.78f, 5.563f, 3.399f, 6.677f)
            curveTo(3.391f, 6.7f, 3.384f, 6.723f, 3.376f, 6.747f)
            curveTo(3.774f, 6.626f, 4.189f, 6.548f, 4.608f, 6.494f)
            curveTo(5.689f, 6.355f, 7.054f, 6.355f, 8.64f, 6.355f)
            lineTo(8.758f, 6.355f)
            lineTo(15.532f, 6.355f)
            curveTo(17.118f, 6.355f, 18.483f, 6.355f, 19.564f, 6.494f)
            curveTo(19.983f, 6.548f, 20.398f, 6.626f, 20.796f, 6.747f)
            curveTo(20.789f, 6.723f, 20.781f, 6.7f, 20.773f, 6.677f)
            curveTo(20.392f, 5.563f, 19.252f, 4.723f, 17.862f, 4.723f)
            horizontalLineTo(6.311f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(8.672f, 7.542f)
            horizontalLineTo(15.328f)
            curveTo(18.702f, 7.542f, 20.39f, 7.542f, 21.338f, 8.529f)
            curveTo(22.285f, 9.516f, 22.063f, 11.04f, 21.617f, 14.09f)
            lineTo(21.194f, 16.981f)
            curveTo(20.844f, 19.372f, 20.669f, 20.568f, 19.772f, 21.284f)
            curveTo(18.875f, 22f, 17.551f, 22f, 14.905f, 22f)
            horizontalLineTo(9.095f)
            curveTo(6.449f, 22f, 5.126f, 22f, 4.228f, 21.284f)
            curveTo(3.331f, 20.568f, 3.156f, 19.372f, 2.806f, 16.981f)
            lineTo(2.384f, 14.09f)
            curveTo(1.937f, 11.04f, 1.714f, 9.516f, 2.662f, 8.529f)
            curveTo(3.61f, 7.542f, 5.298f, 7.542f, 8.672f, 7.542f)
            close()
            moveTo(8f, 18f)
            curveTo(8f, 17.586f, 8.373f, 17.25f, 8.833f, 17.25f)
            horizontalLineTo(15.167f)
            curveTo(15.627f, 17.25f, 16f, 17.586f, 16f, 18f)
            curveTo(16f, 18.414f, 15.627f, 18.75f, 15.167f, 18.75f)
            horizontalLineTo(8.833f)
            curveTo(8.373f, 18.75f, 8f, 18.414f, 8f, 18f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun FilesPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Files, contentDescription = null)
    }
}
