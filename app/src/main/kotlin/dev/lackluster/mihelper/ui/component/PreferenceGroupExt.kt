package dev.lackluster.mihelper.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import dev.lackluster.hyperx.compose.base.CardColors
import dev.lackluster.hyperx.compose.base.CardDefaults
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import top.yukonga.miuix.kmp.theme.MiuixTheme


fun LazyListScope.itemPreferenceGroup(
    key: Any? = null,
    titleResId: Int? = null,
    first: Boolean = false,
    last: Boolean = false,
    visible: Boolean = true,
    titleColor: Color? = null,
    cardColor: CardColors? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        item(
            key = key ?: (titleResId.hashCode() + content.hashCode())
        ) {
            Column(
                modifier = Modifier.animateItem(
                    fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = IntOffset.VisibilityThreshold,
                    ),
                    fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow),
                )
            ) {
                PreferenceGroup(
                    title = titleResId?.let { stringResource(it) },
                    first = first,
                    last = last,
                    titleColor = titleColor ?: MiuixTheme.colorScheme.onBackgroundVariant,
                    cardColor = cardColor ?: CardDefaults.cardColors(),
                    content = content
                )
            }
        }
    }
}

fun LazyListScope.itemAnimated(
    key: Any? = null,
    titleResId: Int? = null,
    visible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (visible) {
        item(
            key = key ?: (titleResId.hashCode() + content.hashCode())
        ) {
            Column(
                modifier = Modifier.animateItem(
                    fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = IntOffset.VisibilityThreshold,
                    ),
                    fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow),
                ),
                content = content
            )
        }
    }
}