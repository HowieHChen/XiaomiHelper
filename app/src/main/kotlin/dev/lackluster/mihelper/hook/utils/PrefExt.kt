package dev.lackluster.mihelper.hook.utils

import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

inline fun PreferenceKey<Boolean>.ifTrue(
    noinline extraCondition: (() -> Boolean)? = null,
    crossinline block: () -> Unit
) {
    val conditionResult = extraCondition?.invoke() ?: true
    if (this.get() && conditionResult) {
        block()
    }
}

inline fun Boolean.ifTrue(
    noinline extraCondition: (() -> Boolean)? = null,
    crossinline block: () -> Unit
) {
    val conditionResult = extraCondition?.invoke() ?: true
    if (this && conditionResult) {
        block()
    }
}