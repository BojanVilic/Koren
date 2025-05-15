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

val KorenIcons.Warning: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Warning",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 1024f,
        viewportHeight = 1024f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(512f, 64f)
            arcToRelative(448f, 448f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 896f)
            arcToRelative(448f, 448f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -896f)
            close()
            moveTo(512f, 256f)
            arcToRelative(58.4f, 58.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, -58.2f, 63.7f)
            lineToRelative(23.4f, 256.4f)
            arcToRelative(35.1f, 35.1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 69.8f, 0f)
            lineToRelative(23.3f, -256.4f)
            arcTo(58.4f, 58.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 512f, 256f)
            close()
            moveTo(512f, 768f)
            arcToRelative(51.2f, 51.2f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -102.4f)
            arcToRelative(51.2f, 51.2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 102.4f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun WarningPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Warning, contentDescription = null)
    }
}
