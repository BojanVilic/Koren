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

val KorenIcons.RotateCamera: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "RotateCamera",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF1C274C)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(14.222f, 21f)
            horizontalLineTo(9.778f)
            curveTo(6.657f, 21f, 5.096f, 21f, 3.975f, 20.265f)
            curveTo(3.49f, 19.946f, 3.073f, 19.537f, 2.749f, 19.061f)
            curveTo(2f, 17.96f, 2f, 16.428f, 2f, 13.364f)
            curveTo(2f, 10.299f, 2f, 8.767f, 2.749f, 7.667f)
            curveTo(3.073f, 7.19f, 3.49f, 6.781f, 3.975f, 6.463f)
            curveTo(4.696f, 5.99f, 5.597f, 5.821f, 6.978f, 5.761f)
            curveTo(7.637f, 5.761f, 8.204f, 5.271f, 8.333f, 4.636f)
            curveTo(8.527f, 3.685f, 9.378f, 3f, 10.366f, 3f)
            horizontalLineTo(13.634f)
            curveTo(14.622f, 3f, 15.473f, 3.685f, 15.667f, 4.636f)
            curveTo(15.796f, 5.271f, 16.363f, 5.761f, 17.022f, 5.761f)
            curveTo(18.403f, 5.821f, 19.304f, 5.99f, 20.025f, 6.463f)
            curveTo(20.51f, 6.781f, 20.927f, 7.19f, 21.251f, 7.667f)
            curveTo(22f, 8.767f, 22f, 10.299f, 22f, 13.364f)
            curveTo(22f, 16.428f, 22f, 17.96f, 21.251f, 19.061f)
            curveTo(20.927f, 19.537f, 20.51f, 19.946f, 20.025f, 20.265f)
            curveTo(18.904f, 21f, 17.343f, 21f, 14.222f, 21f)
            close()
            moveTo(15.27f, 9.6f)
            curveTo(15.27f, 9.185f, 14.934f, 8.85f, 14.52f, 8.85f)
            curveTo(14.106f, 8.85f, 13.77f, 9.185f, 13.77f, 9.6f)
            verticalLineTo(9.743f)
            curveTo(12.3f, 8.999f, 10.458f, 9.241f, 9.23f, 10.469f)
            curveTo(7.7f, 11.999f, 7.7f, 14.48f, 9.23f, 16.01f)
            curveTo(10.759f, 17.54f, 13.24f, 17.54f, 14.77f, 16.01f)
            curveTo(15.421f, 15.359f, 15.796f, 14.534f, 15.892f, 13.684f)
            curveTo(15.939f, 13.273f, 15.643f, 12.901f, 15.232f, 12.854f)
            curveTo(14.82f, 12.808f, 14.449f, 13.103f, 14.402f, 13.515f)
            curveTo(14.342f, 14.039f, 14.112f, 14.547f, 13.709f, 14.949f)
            curveTo(12.765f, 15.893f, 11.234f, 15.893f, 10.29f, 14.949f)
            curveTo(9.346f, 14.005f, 9.346f, 12.474f, 10.29f, 11.53f)
            curveTo(10.938f, 10.882f, 11.862f, 10.679f, 12.683f, 10.92f)
            curveTo(12.374f, 11.064f, 12.193f, 11.406f, 12.266f, 11.754f)
            curveTo(12.351f, 12.16f, 12.749f, 12.419f, 13.155f, 12.333f)
            lineTo(14.674f, 12.014f)
            curveTo(15.021f, 11.94f, 15.27f, 11.634f, 15.27f, 11.28f)
            verticalLineTo(9.6f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun RotateCameraPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.RotateCamera, contentDescription = null)
    }
}
