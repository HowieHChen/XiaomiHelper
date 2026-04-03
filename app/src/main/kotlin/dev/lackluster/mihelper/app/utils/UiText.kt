package dev.lackluster.mihelper.app.utils

import androidx.annotation.StringRes
import dev.lackluster.mihelper.app.state.UiText

fun String?.toUiText(): UiText {
    return UiText.DynamicString(this ?: "")
}

fun @receiver:StringRes Int.toUiText(vararg args: Any): UiText {
    return UiText.StringResource(this, *args)
}