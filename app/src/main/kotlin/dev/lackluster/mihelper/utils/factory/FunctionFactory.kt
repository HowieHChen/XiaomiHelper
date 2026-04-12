@file:Suppress("unused", "DEPRECATION")

package dev.lackluster.mihelper.utils.factory

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import dev.lackluster.mihelper.utils.MLog

/**
 * System dark mode is enabled or not
 *
 * 系统深色模式是否开启
 * @return [Boolean] Whether to enable / 是否开启
 */
val Context.isSystemInDarkMode get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * System dark mode is disabled or not
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

/**
 * pxInt to dp
 *
 * pxInt 转换为 dp
 * @param context using instance / 使用的实例
 * @return [Int]
 */
fun Number.px(context: Context) = (toFloat() / context.resources.displayMetrics.density).toInt()


@SuppressLint("DiscouragedApi")
fun Context.getResId(name: String, defType: String, pkg: String): Int {
    return runCatching {
        resources.getIdentifier(name, defType, pkg)
    }.onFailure {
        MLog.e { "Resource not found: $pkg:$defType/$name" }
    }.getOrDefault(0)
}

fun Number.dp2sp(context: Context) =
    toFloat() * context.resources.displayMetrics.density / context.resources.displayMetrics.scaledDensity