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

val KorenIcons.Image: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Image",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(17.291f, 11.969f)
            curveTo(17.291f, 12.707f, 16.698f, 13.306f, 15.968f, 13.306f)
            curveTo(15.237f, 13.306f, 14.645f, 12.707f, 14.645f, 11.969f)
            curveTo(14.645f, 11.23f, 15.237f, 10.631f, 15.968f, 10.631f)
            curveTo(16.698f, 10.631f, 17.291f, 11.23f, 17.291f, 11.969f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(18.132f, 7.408f)
            curveTo(17.283f, 7.287f, 16.19f, 7.287f, 14.827f, 7.287f)
            horizontalLineTo(9.173f)
            curveTo(7.81f, 7.287f, 6.717f, 7.287f, 5.868f, 7.408f)
            curveTo(4.991f, 7.533f, 4.26f, 7.801f, 3.716f, 8.429f)
            curveTo(3.173f, 9.056f, 3.007f, 9.825f, 3f, 10.721f)
            curveTo(2.994f, 11.587f, 3.139f, 12.683f, 3.319f, 14.05f)
            lineTo(3.684f, 16.821f)
            curveTo(3.825f, 17.889f, 3.939f, 18.754f, 4.116f, 19.431f)
            curveTo(4.301f, 20.135f, 4.573f, 20.72f, 5.084f, 21.172f)
            curveTo(5.595f, 21.624f, 6.203f, 21.82f, 6.918f, 21.912f)
            curveTo(7.605f, 22f, 8.468f, 22f, 9.533f, 22f)
            horizontalLineTo(14.467f)
            curveTo(15.532f, 22f, 16.395f, 22f, 17.082f, 21.912f)
            curveTo(17.797f, 21.82f, 18.405f, 21.624f, 18.916f, 21.172f)
            curveTo(19.427f, 20.72f, 19.699f, 20.135f, 19.884f, 19.431f)
            curveTo(20.061f, 18.754f, 20.175f, 17.889f, 20.316f, 16.821f)
            lineTo(20.681f, 14.05f)
            curveTo(20.861f, 12.683f, 21.006f, 11.587f, 21f, 10.721f)
            curveTo(20.993f, 9.825f, 20.827f, 9.056f, 20.284f, 8.429f)
            curveTo(19.74f, 7.801f, 19.009f, 7.533f, 18.132f, 7.408f)
            close()
            moveTo(6.053f, 8.732f)
            curveTo(5.326f, 8.836f, 4.958f, 9.024f, 4.711f, 9.31f)
            curveTo(4.464f, 9.595f, 4.328f, 9.988f, 4.323f, 10.731f)
            curveTo(4.317f, 11.492f, 4.448f, 12.495f, 4.637f, 13.925f)
            lineTo(4.687f, 14.304f)
            lineTo(5.058f, 14.032f)
            curveTo(6.017f, 13.33f, 7.434f, 13.364f, 8.346f, 14.127f)
            lineTo(11.73f, 16.96f)
            curveTo(12.05f, 17.228f, 12.601f, 17.278f, 12.999f, 17.044f)
            lineTo(13.234f, 16.906f)
            curveTo(14.359f, 16.244f, 15.868f, 16.313f, 16.906f, 17.096f)
            lineTo(18.738f, 18.476f)
            curveTo(18.828f, 17.98f, 18.909f, 17.371f, 19.011f, 16.6f)
            lineTo(19.363f, 13.925f)
            curveTo(19.552f, 12.495f, 19.683f, 11.492f, 19.677f, 10.731f)
            curveTo(19.672f, 9.988f, 19.536f, 9.595f, 19.289f, 9.31f)
            curveTo(19.042f, 9.024f, 18.674f, 8.836f, 17.947f, 8.732f)
            curveTo(17.202f, 8.626f, 16.202f, 8.625f, 14.775f, 8.625f)
            horizontalLineTo(9.225f)
            curveTo(7.798f, 8.625f, 6.798f, 8.626f, 6.053f, 8.732f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(8.859f, 2f)
            horizontalLineTo(15.141f)
            curveTo(15.35f, 2f, 15.511f, 2f, 15.651f, 2.015f)
            curveTo(16.648f, 2.124f, 17.464f, 2.79f, 17.81f, 3.687f)
            horizontalLineTo(6.19f)
            curveTo(6.536f, 2.79f, 7.352f, 2.124f, 8.349f, 2.015f)
            curveTo(8.489f, 2f, 8.65f, 2f, 8.859f, 2f)
            close()
        }
        path(fill = SolidColor(Color(0xFF1C274C))) {
            moveTo(6.879f, 4.5f)
            curveTo(5.628f, 4.5f, 4.602f, 5.34f, 4.259f, 6.454f)
            curveTo(4.252f, 6.477f, 4.245f, 6.5f, 4.239f, 6.524f)
            curveTo(4.597f, 6.403f, 4.97f, 6.324f, 5.347f, 6.271f)
            curveTo(6.32f, 6.132f, 7.549f, 6.132f, 8.976f, 6.132f)
            lineTo(9.083f, 6.132f)
            lineTo(15.179f, 6.132f)
            curveTo(16.606f, 6.132f, 17.835f, 6.132f, 18.807f, 6.271f)
            curveTo(19.185f, 6.324f, 19.558f, 6.403f, 19.916f, 6.524f)
            curveTo(19.91f, 6.5f, 19.903f, 6.477f, 19.896f, 6.454f)
            curveTo(19.553f, 5.34f, 18.527f, 4.5f, 17.275f, 4.5f)
            horizontalLineTo(6.879f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun ImagePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Image, contentDescription = null)
    }
}
