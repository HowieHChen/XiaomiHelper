package dev.lackluster.mihelper.app.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.HorizontalDivider

@Composable
fun InnerHorizontalDivider(
    modifier: Modifier = Modifier
) {
    val color =
        if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.2f)
        else Color.Black.copy(alpha = 0.1f)
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 0.75.dp,
        color = color
    )
}