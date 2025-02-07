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

val KorenIcons.AccountUnselected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "AccountUnselected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 36f,
        viewportHeight = 36f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(18f, 17f)
            arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = false, -7f, -7f)
            arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, 18f, 17f)
            close()
            moveTo(18f, 5f)
            arcToRelative(5f, 5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -5f, 5f)
            arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(30.47f, 24.37f)
            arcToRelative(17.16f, 17.16f, 0f, isMoreThanHalf = false, isPositiveArc = false, -24.93f, 0f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, 25.74f)
            verticalLineTo(31f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 2f)
            horizontalLineTo(29f)
            arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, -2f)
            verticalLineTo(25.74f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 30.47f, 24.37f)
            close()
            moveTo(29f, 31f)
            horizontalLineTo(7f)
            verticalLineTo(25.73f)
            arcToRelative(15.17f, 15.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 0f)
            horizontalLineToRelative(0f)
            close()
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
private fun AccountUnselectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.AccountUnselected, contentDescription = null)
    }
}
