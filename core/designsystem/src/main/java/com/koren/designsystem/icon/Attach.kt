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

val KorenIcons.Attach: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Attach",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(20.647f, 10.616f)
            lineTo(11.885f, 19.378f)
            curveTo(9.933f, 21.33f, 6.767f, 21.33f, 4.814f, 19.378f)
            curveTo(2.862f, 17.425f, 2.862f, 14.259f, 4.814f, 12.307f)
            lineTo(12.946f, 4.175f)
            curveTo(14.313f, 2.808f, 16.529f, 2.808f, 17.896f, 4.175f)
            curveTo(19.263f, 5.542f, 19.263f, 7.758f, 17.896f, 9.125f)
            lineTo(10.102f, 16.918f)
            curveTo(9.321f, 17.699f, 8.055f, 17.699f, 7.274f, 16.918f)
            curveTo(6.493f, 16.137f, 6.493f, 14.871f, 7.274f, 14.09f)
            lineTo(14.468f, 6.896f)
        }
    }.build()
}

@Preview
@Composable
private fun AttachPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Attach, contentDescription = null)
    }
}
