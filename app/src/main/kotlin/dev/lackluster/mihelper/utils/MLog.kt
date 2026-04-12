package dev.lackluster.mihelper.utils

import android.util.Log
import dev.lackluster.mihelper.BuildConfig
import io.github.libxposed.api.XposedModule

object MLog {
    const val DEFAULT_TAG = "MiHelper"

    @Volatile
    private var module: XposedModule? = null

    @Volatile
    var isDebugEnabled: Boolean = BuildConfig.DEBUG
        set(value) {
            if (field == value) return
            if (!value) {
                d { "Stop logging" }
            }
            field = value
            if (value) {
                d { "Start logging" }
            }
        }

    fun init(module: XposedModule) {
        this.module = module
    }

    inline fun v(tag: String = DEFAULT_TAG, msg: () -> String) {
        if (isDebugEnabled) printLog(Log.VERBOSE, tag, msg())
    }

    inline fun d(tag: String = DEFAULT_TAG, msg: () -> String) {
        if (isDebugEnabled) printLog(Log.DEBUG, tag, msg())
    }

    inline fun i(tag: String = DEFAULT_TAG, msg: () -> String) {
        printLog(Log.INFO, tag, msg())
    }

    inline fun w(tag: String = DEFAULT_TAG, msg: () -> String) {
        printLog(Log.WARN, tag, msg())
    }

    inline fun e(tag: String = DEFAULT_TAG, t: Throwable? = null, msg: () -> String) {
        printLog(Log.ERROR, tag, msg(), t)
    }

    fun e(t: Throwable, tag: String = DEFAULT_TAG) {
        printLog(Log.ERROR, tag, Log.getStackTraceString(t), t)
    }

    @PublishedApi
    internal fun printLog(priority: Int, tag: String, msg: String, t: Throwable? = null) {
        when (priority) {
            Log.VERBOSE -> Log.v(tag, msg, t)
            Log.DEBUG -> Log.d(tag, msg, t)
            Log.INFO -> Log.i(tag, msg, t)
            Log.WARN -> Log.w(tag, msg, t)
            Log.ERROR -> Log.e(tag, msg, t)
        }

        try {
            if (t != null) {
                module?.log(priority, tag, msg, t)
            } else {
                module?.log(priority, tag, msg)
            }
        } catch (_: Throwable) { }
    }
}