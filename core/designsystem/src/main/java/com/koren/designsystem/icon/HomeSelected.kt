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

val KorenIcons.HomeSelected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "HomeSelected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(2.519f, 7.823f)
            curveTo(2f, 8.771f, 2f, 9.915f, 2f, 12.204f)
            verticalLineTo(13.725f)
            curveTo(2f, 17.626f, 2f, 19.576f, 3.172f, 20.788f)
            curveTo(4.343f, 22f, 6.229f, 22f, 10f, 22f)
            horizontalLineTo(14f)
            curveTo(17.771f, 22f, 19.657f, 22f, 20.828f, 20.788f)
            curveTo(22f, 19.576f, 22f, 17.626f, 22f, 13.725f)
            verticalLineTo(12.204f)
            curveTo(22f, 9.915f, 22f, 8.771f, 21.481f, 7.823f)
            curveTo(20.962f, 6.874f, 20.013f, 6.286f, 18.116f, 5.108f)
            lineTo(16.116f, 3.867f)
            curveTo(14.111f, 2.622f, 13.108f, 2f, 12f, 2f)
            curveTo(10.892f, 2f, 9.889f, 2.622f, 7.884f, 3.867f)
            lineTo(5.884f, 5.108f)
            curveTo(3.987f, 6.286f, 3.038f, 6.874f, 2.519f, 7.823f)
            close()
            moveTo(9f, 17.25f)
            curveTo(8.586f, 17.25f, 8.25f, 17.586f, 8.25f, 18f)
            curveTo(8.25f, 18.414f, 8.586f, 18.75f, 9f, 18.75f)
            horizontalLineTo(15f)
            curveTo(15.414f, 18.75f, 15.75f, 18.414f, 15.75f, 18f)
            curveTo(15.75f, 17.586f, 15.414f, 17.25f, 15f, 17.25f)
            horizontalLineTo(9f)
            close()
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun HomeSelectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.HomeSelected, contentDescription = null)
    }
}
