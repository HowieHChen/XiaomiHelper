package dev.lackluster.mihelper.utils

import android.annotation.SuppressLint
import androidx.annotation.Keep
import java.lang.reflect.Method

@SuppressLint("PrivateApi")
@Keep
object SystemProperties {
    private const val PROP_NAME_MAX = 31
    private const val PROP_VALUE_MAX = 91
    private const val TAG = "SystemProperties"
    private var classSystemProperties: Class<*>?
    private var isSupportGet = false
    private var isSupportGetBoolean = false
    private var isSupportGetInt = false
    private var isSupportGetLong = false
    private var isSupportSet = false
    private var mGet: Method? = null
    private var mGetInt: Method? = null
    private var mGetLong: Method? = null
    private var mGetBoolean: Method? = null
    private var mSet: Method? = null

    init {
        classSystemProperties = try {
            Class.forName("android.os.SystemProperties")
        } catch (_: Exception) {
            null
        }
        val cls = classSystemProperties
        if (cls != null) {
            isSupportGet = try {
                mGet = cls.getMethod("get", String::class.java, String::class.java)
                mGet != null
            } catch (_: Exception) {
                false
            }
            isSupportGetInt = try {
                mGetInt = cls.getMethod("getInt", String::class.java, java.lang.Integer::class.java)
                mGetInt != null
            } catch (_: Exception) {
                false
            }
            isSupportGetLong = try {
                mGetLong = cls.getMethod("getLong", String::class.java, java.lang.Long::class.java)
                mGetLong != null
            } catch (_: Exception) {
                false
            }
            isSupportGetBoolean = try {
                mGetBoolean = cls.getMethod("getBoolean", String::class.java, java.lang.Boolean::class.java)
                mGetBoolean != null
            } catch (_: Exception) {
                false
            }
            isSupportSet = try {
                mSet = cls.getMethod("set", String::class.java)
                mSet != null
            } catch (_: Exception) {
                false
            }
        }
    }

    fun get(key: String, defValue: String): String {
        if (isSupportGet) {
            try {
                return mGet?.invoke(null, key, defValue) as? String ?: defValue
            } catch (_: Exception) {
            }
        }
        return defValue
    }

    fun get(key: String): String {
        return get(key, "")
    }

    fun getInt(key: String, defValue: Int): Int {
        if (isSupportGetInt) {
            try {
                return mGetInt?.invoke(null, key, defValue) as? Int ?: defValue
            } catch (_: Exception){
            }
        }
        return defValue
    }

    fun getLong(key: String, defValue: Long): Long {
        if (isSupportGetLong) {
            try {
                return mGetLong?.invoke(null, key, defValue) as? Long ?: defValue
            } catch (_: Exception) {
            }
        }
        return defValue
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        if (isSupportGetBoolean) {
            try {
                return mGetBoolean?.invoke(null, key, defValue) as? Boolean ?: defValue
            } catch (_: Exception) {
            }
        }
        return defValue
    }

    fun set(key: String, value: String?) {
        if (isSupportSet) {
            if (key.length > PROP_NAME_MAX) {
                throw IllegalArgumentException("key.length > 31")
            }
            if (value != null && value.length > PROP_VALUE_MAX) {
                throw IllegalArgumentException("val.length > 91")
            }
            try {
                mSet?.invoke(null, key, value)
            } catch (_: Exception) {
            }
        }
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
