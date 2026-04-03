package dev.lackluster.mihelper.app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import dev.lackluster.mihelper.R
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun EmptyPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val foregroundColor: Color
        val backgroundColor: Color
        MiuixTheme.colorScheme.onBackground.let {
            if (it.luminance() >= 0.5f) {
                foregroundColor = it.copy(alpha = 0.2f)
                backgroundColor = it.copy(alpha = 0.12f)
            } else {
                foregroundColor = it.copy(alpha = 0.1f)
                backgroundColor = it.copy(alpha = 0.06f)
            }
        }
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.empty_page_background),
            contentDescription = null,
            colorFilter = ColorFilter.tint(backgroundColor),
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.empty_page_foreground),
            contentDescription = null,
            colorFilter = ColorFilter.tint(foregroundColor),
            contentScale = ContentScale.Inside
        )
    }
}