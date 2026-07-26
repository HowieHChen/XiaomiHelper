package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import android.app.Application
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.CommonGesture
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.toTyped

object LineGesture : StaticHooker() {
    private val actionLongPress by Preferences.MiuiHome.LINE_GESTURE_LONG_PRESS.lazyGet()
    private val actionDoubleTap by Preferences.MiuiHome.LINE_GESTURE_DOUBLE_TAP.lazyGet()
    private val actionSingleTap by Preferences.MiuiHome.LINE_GESTURE_SINGLE_TAP.lazyGet()

    private val metGetInstance by lazy {
        "com.miui.home.launcher.Application".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getInstance"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Application>()
    }

    override fun onInit() {
        updateSelfState(actionLongPress != 0 || actionDoubleTap != 0 || actionSingleTap != 0)
    }

    override fun onHook() {
        "com.miui.home.recents.gesture.NavStubGestureEventManager".toClassOrNull()?.apply {
            if (actionLongPress != 0) {
                resolve().firstMethodOrNull {
                    name = "handleLongPressEvent"
                }?.hook {
                    val application = metGetInstance?.invoke(null)
                    if (application != null) {
                        CommonGesture.doAction(application, actionLongPress)
                        result(null)
                    } else {
                        result(proceed())
                    }
                }
            }
            if (actionDoubleTap != 0) {
                resolve().firstMethodOrNull {
                    name = "handleDoubleClickEvent"
                }?.hook {
                    val application = metGetInstance?.invoke(null)
                    if (application != null) {
                        CommonGesture.doAction(application, actionDoubleTap)
                        result(null)
                    } else {
                        result(proceed())
                    }
                }
                val fldIsCanDoubleClickTriggerApp = resolve().firstFieldOrNull {
                    name = "isCanDoubleClickTriggerApp"
                }?.toTyped<Boolean>()
                resolve().firstMethodOrNull {
                    name = "checkDoubleClickTriggerApp"
                }?.hook {
                    fldIsCanDoubleClickTriggerApp?.set(thisObject, true)
                    result(null)
                }
                resolve().firstMethodOrNull {
                    name = "updateIsCanDoubleClickTriggerApp"
                }?.hook {
                    result(null)
                }
            }
            if (actionSingleTap != 0) {
                val clzNavStubGestureListener = "com.miui.home.recents.gesture.NavStubGestureListener".toClassOrNull()
                $$"android.view.MiuiGestureDetector$SimpleOnGestureListener".toClassOrNull()?.apply {
                    resolve().firstMethodOrNull {
                        name = "onSingleTapConfirmed"
                    }?.hook {
                        if (clzNavStubGestureListener?.isInstance(thisObject) != true) {
                            return@hook result(proceed())
                        }
                        val application = metGetInstance?.invoke(null)
                        if (application != null) {
                            CommonGesture.doAction(application, actionSingleTap)
                            result(true)
                        } else {
                            result(proceed())
                        }
                    }
                }
            }
        }
    }
}