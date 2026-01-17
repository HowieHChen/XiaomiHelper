@file:Suppress("unused", "DEPRECATION")

package dev.lackluster.mihelper.utils.factory

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.provider.Settings
import dev.lackluster.mihelper.utils.Prefs
import androidx.core.net.toUri
import de.robv.android.xposed.XposedHelpers


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

/**
 * pxInt to dp
 *
 * pxInt 转换为 dp
 * @param context using instance / 使用的实例
 * @return [Int]
 */
fun Number.px(context: Context) = (toFloat() / context.resources.displayMetrics.density).toInt()

@SuppressLint("WorldReadableFiles")
fun getSP(context: Context, prefName: String = Prefs.NAME): SharedPreferences {
    return context.getSharedPreferences(prefName, Activity.MODE_WORLD_READABLE)
}

@SuppressLint("DiscouragedApi")
fun Application.getResID(name: String, defType: String, pkg: String): Int {
    return try {
        this.resources.getIdentifier(name, defType, pkg)
    } catch (t: Throwable) {
        0
    }
}

@SuppressLint("DiscouragedApi")
fun Context.getResID(name: String, defType: String, pkg: String): Int {
    return try {
        this.resources.getIdentifier(name, defType, pkg)
    } catch (t: Throwable) {
        0
    }
}

fun Number.dp2sp(context: Context) =
    toFloat() * context.resources.displayMetrics.density / context.resources.displayMetrics.scaledDensity

fun Context.jumpToAppDetailsSettings(pkg: String) {
    val packageURI = "package:${pkg}".toUri()
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
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

fun Any.setAdditionalInstanceField(
    fieldName: String,
    value: Any?
) {
    XposedHelpers.setAdditionalInstanceField(this, fieldName, value)
}

inline fun <reified T: Any?> Any.getAdditionalInstanceField(
    fieldName: String,
    defValue: T? = null
): T? {
    return XposedHelpers.getAdditionalInstanceField(this, fieldName) as? T ?: defValue
}