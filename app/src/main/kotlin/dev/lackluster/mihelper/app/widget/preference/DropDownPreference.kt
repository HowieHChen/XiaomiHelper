package dev.lackluster.mihelper.app.widget.preference

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.component.ImageSource
import dev.lackluster.hyperx.ui.preference.DropDownEntry
import dev.lackluster.hyperx.ui.preference.DropDownMode
import dev.lackluster.hyperx.ui.preference.DropDownPreference
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.toImageSource
import dev.lackluster.mihelper.app.utils.toUiText
import kotlin.collections.mapIndexed

data class DropDownOption<T>(
    val value: T,
    val title: UiText,
    val summary: UiText? = null,
    val icon: ImageSource? = null,
    val iconTint: Color? = null
) {
    constructor(
        value: T,
        @StringRes titleRes: Int,
        @StringRes summaryRes: Int? = null,
        @DrawableRes iconRes: Int? = null,
        iconTint: Color? = null
    ) : this(value, titleRes.toUiText(), summaryRes?.toUiText(), iconRes?.toImageSource(), iconTint)

    constructor(
        value: T,
        title: String,
        summary: String? = null,
        icon: ImageSource? = null,
        iconTint: Color? = null
    ) : this(value, title.toUiText(), summary?.toUiText(), icon, iconTint)
}

@Composable
fun <T : Any> DropDownPreference(
    title: String,
    value: T,
    options: List<DropDownOption<T>>,
    onValueChange: (T) -> Unit,
    icon: ImageIcon? = null,
    summary: String? = null,
    mode: DropDownMode = DropDownMode.Popup,
    showValue: Boolean = true,
    enabled: Boolean = true,
) {
    val resolvedTitles = options.map { entry ->
        entry.title.asString()
    }
    val resolvedSummaries = options.map { entry ->
        entry.summary?.asString()
    }
    val wrappedEntries = remember(options, resolvedTitles, resolvedSummaries) {
        options.mapIndexed { index, option ->
            DropDownEntry(
                value = option.value,
                title = resolvedTitles[index],
                summary = resolvedSummaries[index],
                icon = option.icon,
                iconTint = option.iconTint
            )
        }
    }

    DropDownPreference(
        title = title,
        value = value,
        entries = wrappedEntries,
        onValueChange = onValueChange,
        icon = icon,
        summary = summary,
        mode = mode,
        showValue = showValue,
        enabled = enabled,
    )
}

@Composable
fun <T: Any> DropDownPreference(
    key: PreferenceKey<T>,
    title: String,
    options: List<DropDownOption<T>>,
    icon: ImageIcon? = null,
    summary: String? = null,
    mode: DropDownMode = DropDownMode.Popup,
    showValue: Boolean = true,
    enabled: Boolean = true,
    onValueChange: (T) -> Unit = {},
) {
    val resolvedTitles = options.map { entry ->
        entry.title.asString()
    }
    val resolvedSummaries = options.map { entry ->
        entry.summary?.asString()
    }
    val wrappedEntries = remember(options, resolvedTitles, resolvedSummaries) {
        options.mapIndexed { index, option ->
            DropDownEntry(
                value = option.value,
                title = resolvedTitles[index],
                summary = resolvedSummaries[index],
                icon = option.icon,
                iconTint = option.iconTint
            )
        }
    }

    DropDownPreference(
        key = key,
        title = title,
        entries = wrappedEntries,
        icon = icon,
        summary = summary,
        mode = mode,
        showValue = showValue,
        enabled = enabled,
        onValueChange = onValueChange
    )
}