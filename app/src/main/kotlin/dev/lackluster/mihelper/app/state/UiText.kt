package dev.lackluster.mihelper.app.state

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText

    class Combined(
        val first: UiText,
        val second: UiText,
        val separator: String = ""
    ) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = resId, formatArgs = args)
            is Combined -> first.asString() + separator + second.asString()
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
            is Combined -> first.asString(context) + separator + second.asString(context)
        }
    }

    operator fun plus(other: UiText): UiText {
        return Combined(this, other, separator = "")
    }
}