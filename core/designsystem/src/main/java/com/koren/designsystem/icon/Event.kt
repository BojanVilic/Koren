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

val KorenIcons.Event: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Event",
        defaultWidth = 800.dp,
        defaultHeight = 800.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(7f, 2f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 1f)
            verticalLineToRelative(1.001f)
            curveToRelative(-0.961f, 0.014f, -1.34f, 0.129f, -1.721f, 0.333f)
            arcToRelative(2.272f, 2.272f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.945f, 0.945f)
            curveTo(3.116f, 5.686f, 3f, 6.09f, 3f, 7.205f)
            verticalLineToRelative(10.59f)
            curveToRelative(0f, 1.114f, 0.116f, 1.519f, 0.334f, 1.926f)
            curveToRelative(0.218f, 0.407f, 0.538f, 0.727f, 0.945f, 0.945f)
            curveToRelative(0.407f, 0.218f, 0.811f, 0.334f, 1.926f, 0.334f)
            horizontalLineToRelative(11.59f)
            curveToRelative(1.114f, 0f, 1.519f, -0.116f, 1.926f, -0.334f)
            curveToRelative(0.407f, -0.218f, 0.727f, -0.538f, 0.945f, -0.945f)
            curveToRelative(0.218f, -0.407f, 0.334f, -0.811f, 0.334f, -1.926f)
            lineTo(21f, 7.205f)
            curveToRelative(0f, -1.115f, -0.116f, -1.519f, -0.334f, -1.926f)
            arcToRelative(2.272f, 2.272f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.945f, -0.945f)
            curveTo(19.34f, 4.13f, 18.961f, 4.015f, 18f, 4f)
            lineTo(18f, 3f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, -2f, 0f)
            verticalLineToRelative(1f)
            lineTo(8f, 4f)
            lineTo(8f, 3f)
            arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, -1f)
            close()
            moveTo(5f, 9f)
            verticalLineToRelative(8.795f)
            curveToRelative(0f, 0.427f, 0.019f, 0.694f, 0.049f, 0.849f)
            curveToRelative(0.012f, 0.06f, 0.017f, 0.074f, 0.049f, 0.134f)
            arcToRelative(0.275f, 0.275f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.124f, 0.125f)
            curveToRelative(0.06f, 0.031f, 0.073f, 0.036f, 0.134f, 0.048f)
            curveToRelative(0.155f, 0.03f, 0.422f, 0.049f, 0.849f, 0.049f)
            horizontalLineToRelative(11.59f)
            curveToRelative(0.427f, 0f, 0.694f, -0.019f, 0.849f, -0.049f)
            arcToRelative(0.353f, 0.353f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.134f, -0.049f)
            arcToRelative(0.275f, 0.275f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.125f, -0.124f)
            arcToRelative(0.353f, 0.353f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.048f, -0.134f)
            curveToRelative(0.03f, -0.155f, 0.049f, -0.422f, 0.049f, -0.849f)
            lineTo(19.004f, 9f)
            lineTo(5f, 9f)
            close()
            moveTo(13.75f, 13f)
            arcToRelative(0.75f, 0.75f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.75f, 0.75f)
            verticalLineToRelative(2.5f)
            curveToRelative(0f, 0.414f, 0.336f, 0.75f, 0.75f, 0.75f)
            horizontalLineToRelative(2.5f)
            arcToRelative(0.75f, 0.75f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.75f, -0.75f)
            verticalLineToRelative(-2.5f)
            arcToRelative(0.75f, 0.75f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.75f, -0.75f)
            horizontalLineToRelative(-2.5f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun EventPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.Event, contentDescription = null)
    }
}
