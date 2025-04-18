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

val KorenIcons.QrCode: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "QrCode",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(21f, 2f)
            lineTo(15f, 2f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 1f)
            lineTo(14f, 9f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 1f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            lineTo(18f, 10f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            lineTo(22f, 3f)
            arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 21f, 2f)
            close()
            moveTo(18f, 8f)
            lineTo(16f, 8f)
            lineTo(16f, 4f)
            horizontalLineToRelative(4f)
            lineTo(20f, 8f)
            close()
            moveTo(3f, 10f)
            lineTo(9f, 10f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, -1f)
            lineTo(10f, 3f)
            arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9f, 2f)
            lineTo(3f, 2f)
            arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 3f)
            lineTo(2f, 9f)
            arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3f, 10f)
            close()
            moveTo(4f, 4f)
            lineTo(8f, 4f)
            lineTo(8f, 8f)
            lineTo(4f, 8f)
            close()
            moveTo(5f, 16f)
            verticalLineToRelative(2f)
            lineTo(3f, 18f)
            lineTo(3f, 16f)
            close()
            moveTo(3f, 20f)
            lineTo(5f, 20f)
            verticalLineToRelative(2f)
            lineTo(3f, 22f)
            close()
            moveTo(7f, 18f)
            verticalLineToRelative(2f)
            lineTo(5f, 20f)
            lineTo(5f, 18f)
            close()
            moveTo(7f, 16f)
            lineTo(5f, 16f)
            lineTo(5f, 14f)
            lineTo(7f, 14f)
            lineTo(7f, 12f)
            lineTo(9f, 12f)
            verticalLineToRelative(4f)
            close()
            moveTo(5f, 12f)
            verticalLineToRelative(2f)
            lineTo(3f, 14f)
            lineTo(3f, 12f)
            close()
            moveTo(14f, 15f)
            verticalLineToRelative(1f)
            lineTo(13f, 16f)
            lineTo(13f, 14f)
            lineTo(11f, 14f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(3f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 1f)
            horizontalLineToRelative(6f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, -1f)
            lineTo(22f, 15f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, -1f)
            lineTo(16f, 14f)
            lineTo(16f, 12f)
            lineTo(14f, 12f)
            close()
            moveTo(20f, 16f)
            verticalLineToRelative(4f)
            lineTo(16f, 20f)
            lineTo(16f, 16f)
            close()
            moveTo(9f, 18f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(2f)
            lineTo(7f, 22f)
            lineTo(7f, 20f)
            lineTo(9f, 20f)
            close()
            moveTo(13f, 6f)
            lineTo(11f, 6f)
            lineTo(11f, 4f)
            horizontalLineToRelative(2f)
            close()
            moveTo(11f, 8f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(4f)
            lineTo(11f, 12f)
            close()
            moveTo(5f, 5f)
            lineTo(7f, 5f)
            lineTo(7f, 7f)
            lineTo(5f, 7f)
            close()
            moveTo(17f, 5f)
            horizontalLineToRelative(2f)
            lineTo(19f, 7f)
            lineTo(17f, 7f)
            close()
            moveTo(19f, 19f)
            lineTo(17f, 19f)
            lineTo(17f, 17f)
            horizontalLineToRelative(2f)
            close()
        }
    }.build()
}

@Preview
@Composable
private fun QrCodePreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.QrCode, contentDescription = null)
    }
}
