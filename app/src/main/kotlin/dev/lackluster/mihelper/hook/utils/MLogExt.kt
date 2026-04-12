package dev.lackluster.mihelper.hook.utils

import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.utils.MLog

inline fun BaseHooker.v(msg: () -> String) = MLog.v(tag = hookerName, msg = msg)

inline fun BaseHooker.d(msg: () -> String) = MLog.d(tag = hookerName, msg = msg)

inline fun BaseHooker.i(msg: () -> String) = MLog.i(tag = hookerName, msg = msg)

inline fun BaseHooker.w(msg: () -> String) = MLog.w(tag = hookerName, msg = msg)

inline fun BaseHooker.e(t: Throwable? = null, msg: () -> String) = MLog.e(tag = hookerName, t = t, msg = msg)

fun BaseHooker.e(t: Throwable) = MLog.e(t = t, tag = hookerName)