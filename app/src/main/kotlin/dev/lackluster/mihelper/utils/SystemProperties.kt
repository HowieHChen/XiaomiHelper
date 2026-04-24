package dev.lackluster.mihelper.utils

import android.annotation.SuppressLint
import androidx.annotation.Keep
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.hook.utils.toTyped

@Keep
object SystemProperties {
    private const val PROP_NAME_MAX = 31
    private const val PROP_VALUE_MAX = 91
    private const val TAG = "SystemProperties"

    private val clzSystemProperties by lazy {
        try {
            @SuppressLint("PrivateApi")
            Class.forName("android.os.SystemProperties")
        } catch (e: Exception) {
            MLog.e(TAG, e) { "android.os.SystemProperties class not found" }
            null
        }
    }
    private val mGet by lazy {
        clzSystemProperties?.resolve()?.firstMethodOrNull {
            name = "get"
            parameters(String::class, String::class)
        }?.toTyped<String>()
    }
    private val mGetInt by lazy {
        clzSystemProperties?.resolve()?.firstMethodOrNull {
            name = "getInt"
            parameters(String::class, Int::class)
        }?.toTyped<Int>()
    }
    private val mGetLong by lazy {
        clzSystemProperties?.resolve()?.firstMethodOrNull {
            name = "getLong"
            parameters(String::class, Long::class)
        }?.toTyped<Long>()
    }
    private val mGetBoolean by lazy {
        clzSystemProperties?.resolve()?.firstMethodOrNull {
            name = "getBoolean"
            parameters(String::class, Boolean::class)
        }?.toTyped<Boolean>()
    }
    private val mSet by lazy {
        clzSystemProperties?.resolve()?.firstMethodOrNull {
            name = "set"
            parameters(String::class, String::class)
        }?.toTyped<Any?>()
    }

    fun get(key: String, defValue: String): String {
        return mGet?.invoke(null, key, defValue) ?: defValue
    }

    fun get(key: String): String {
        return get(key, "")
    }

    fun getInt(key: String, defValue: Int): Int {
        return mGetInt?.invoke(null, key, defValue) ?: defValue
    }

    fun getLong(key: String, defValue: Long): Long {
        return mGetLong?.invoke(null, key, defValue) ?: defValue
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mGetBoolean?.invoke(null, key, defValue) ?: defValue
    }

    fun set(key: String, value: String?) {
        if (key.length > PROP_NAME_MAX) {
            throw IllegalArgumentException("key.length > 31")
        }
        if (value != null && value.length > PROP_VALUE_MAX) {
            throw IllegalArgumentException("val.length > 91")
        }
        mSet?.invoke(null, key, value)
    }

    fun set(key: String, value: Int) {
        set(key, value.toString())
    }

    fun set(key: String, value: Long) {
        set(key, value.toString())
    }

    fun set(key: String, value: Boolean) {
        set(key, value.toString())
    }
}
