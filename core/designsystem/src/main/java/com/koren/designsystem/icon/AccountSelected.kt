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

val KorenIcons.AccountSelected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "AccountSelected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 36f,
        viewportHeight = 36f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(30.61f, 24.52f)
            arcToRelative(17.16f, 17.16f, 0f, isMoreThanHalf = false, isPositiveArc = false, -25.22f, 0f)
            arcToRelative(1.51f, 1.51f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.39f, 1f)
            verticalLineToRelative(6f)
            arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.5f, 33f)
            horizontalLineToRelative(23f)
            arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 31f, 31.5f)
            verticalLineToRelative(-6f)
            arcTo(1.51f, 1.51f, 0f, isMoreThanHalf = false, isPositiveArc = false, 30.61f, 24.52f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(18f, 10f)
            moveToRelative(-7f, 0f)
            arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, 14f, 0f)
            arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, -14f, 0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0f
        ) {
            moveTo(0f, 0f)
            horizontalLineToRelative(36f)
            verticalLineToRelative(36f)
            horizontalLineToRelative(-36f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun AccountSelectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.AccountSelected, contentDescription = null)
    }
}
