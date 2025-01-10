package com.koren.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun StyledStringResource(
    modifier: Modifier = Modifier,
    @StringRes stringRes: Int,
    formatArgs: List<Pair<String, SpanStyle>>,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            val fullString = stringResource(stringRes, *formatArgs.map { it.first }.toTypedArray())
            var currentIndex = 0
            formatArgs.forEach { (arg, style) ->
                val startIndex = fullString.indexOf(arg, currentIndex)
                if (startIndex != -1) {
                    val endIndex = startIndex + arg.length
                    append(fullString.substring(currentIndex, startIndex))
                    withStyle(style = style) {
                        append(arg)
                    }
                    currentIndex = endIndex
                }
            }
            append(fullString.substring(currentIndex))
        },
        style = style,
        fontWeight = fontWeight ?: style.fontWeight,
        textAlign = textAlign ?: style.textAlign
    )
}