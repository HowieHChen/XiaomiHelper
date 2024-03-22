@file:Suppress("unused", "DEPRECATION")

package dev.lackluster.mihelper.utils.factory

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import dev.lackluster.mihelper.utils.Prefs

/**
 * System dark mode is enabled or not
 *
 * 系统深色模式是否开启
 * @return [Boolean] Whether to enable / 是否开启
 */
val Context.isSystemInDarkMode get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * System dark mode is disable or not
 *
 * 系统深色模式是否没开启
 * @return [Boolean] Whether to enable / 是否开启
 */
inline val Context.isNotSystemInDarkMode get() = isSystemInDarkMode.not()

/**
 * dp to pxInt
 *
 * dp 转换为 pxInt
 * @param context using instance / 使用的实例
 * @return [Int]
 */
fun Number.dp(context: Context) = dpFloat(context).toInt()

/**
 * dp to pxFloat
 *
 * dp 转换为 pxFloat
 * @param context using instance / 使用的实例
 * @return [Float]
 */
fun Number.dpFloat(context: Context) = toFloat() * context.resources.displayMetrics.density

@SuppressLint("WorldReadableFiles")
fun getSP(context: Context, prefName: String = Prefs.NAME): SharedPreferences {
    return context.getSharedPreferences(prefName, Activity.MODE_WORLD_READABLE)
}

inline fun hasEnable(
    key: String,
    default: Boolean = false,
    noinline extraCondition: (() -> Boolean)? = null,
    crossinline block: () -> Unit
) {
    val conditionResult = if (extraCondition != null) extraCondition() else true
    if (Prefs.getBoolean(key, default) && conditionResult) {
        block()
    }
}