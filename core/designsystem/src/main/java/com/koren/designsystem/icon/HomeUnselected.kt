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

val KorenIcons.HomeUnselected: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "HomeUnselected",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fillAlpha = 0.9f,
            stroke = SolidColor(Color(0xFF000000)),
            strokeAlpha = 0.9f,
            strokeLineWidth = 1.5f
        ) {
            moveTo(2f, 12.204f)
            curveTo(2f, 9.915f, 2f, 8.771f, 2.519f, 7.823f)
            curveTo(3.038f, 6.874f, 3.987f, 6.286f, 5.884f, 5.108f)
            lineTo(7.884f, 3.867f)
            curveTo(9.889f, 2.622f, 10.892f, 2f, 12f, 2f)
            curveTo(13.108f, 2f, 14.111f, 2.622f, 16.116f, 3.867f)
            lineTo(18.116f, 5.108f)
            curveTo(20.013f, 6.286f, 20.962f, 6.874f, 21.481f, 7.823f)
            curveTo(22f, 8.771f, 22f, 9.915f, 22f, 12.204f)
            verticalLineTo(13.725f)
            curveTo(22f, 17.626f, 22f, 19.576f, 20.828f, 20.788f)
            curveTo(19.657f, 22f, 17.771f, 22f, 14f, 22f)
            horizontalLineTo(10f)
            curveTo(6.229f, 22f, 4.343f, 22f, 3.172f, 20.788f)
            curveTo(2f, 19.576f, 2f, 17.626f, 2f, 13.725f)
            verticalLineTo(12.204f)
            close()
        }
        path(
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(15f, 18f)
            horizontalLineTo(9f)
        }
    }.build()
}

@Preview(showBackground = true)
@Composable
private fun HomeUnselectedPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KorenIcons.HomeUnselected, contentDescription = null)
    }
}
