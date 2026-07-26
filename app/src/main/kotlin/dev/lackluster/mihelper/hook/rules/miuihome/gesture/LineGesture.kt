package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import android.app.Application
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.CommonGesture
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import kotlinx.coroutines.Runnable

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
    private val clzNavStubView by "com.miui.home.recents.NavStubView".lazyClassOrNull()
    private val fldRecentsAnimationListenerImpl by lazy {
        clzNavStubView?.resolve()?.firstFieldOrNull {
            name = "mRecentsAnimationListenerImpl"
        }?.toTyped<Any>()
    }
    private val metFinishController by lazy {
        "com.miui.home.recents.RecentsAnimationListenerImpl".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "finishController"
            parameters(Boolean::class, Runnable::class, Boolean::class)
        }?.toTyped<Unit>()
    }

    private var Any.navStubView by extraOf<View>("NAV_STUB_VIEW")

    override fun onInit() {
        updateSelfState(actionLongPress != 0 || actionDoubleTap != 0 || actionSingleTap != 0)
    }

    override fun onHook() {
        val foregroundAppRelatedActions = listOf(9, 10, 12)
        if (
            actionDoubleTap in foregroundAppRelatedActions ||
            actionSingleTap in foregroundAppRelatedActions ||
            actionLongPress in foregroundAppRelatedActions
        ) {
            clzNavStubView?.apply {
                val fldNavStubGestureEventManager = resolve().firstFieldOrNull {
                    name = "mNavStubGestureEventManager"
                }?.toTyped<Any>()
                resolve().firstConstructor {
                    parameterCount = 1
                }.hook {
                    val ori = proceed()
                    fldNavStubGestureEventManager?.get(thisObject)?.navStubView = thisObject as? View
                    result(ori)
                }
            }
        }
        "com.miui.home.recents.gesture.NavStubGestureEventManager".toClassOrNull()?.apply {
            if (actionLongPress != 0) {
                resolve().firstMethodOrNull {
                    name = "handleLongPressEvent"
                }?.hook {
                    val application = metGetInstance?.invoke(null)
                    if (application != null) {
                        val doAction = Runnable { CommonGesture.doAction(application, actionLongPress) }
                        val navStubView = thisObject.navStubView
                        if (actionLongPress !in foregroundAppRelatedActions || navStubView == null || !finishRecentsToAppThen(navStubView, doAction::run)) {
                            doAction.run()
                        }
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
                        val doAction = Runnable { CommonGesture.doAction(application, actionDoubleTap) }
                        val navStubView = thisObject.navStubView
                        if (actionDoubleTap !in foregroundAppRelatedActions || navStubView == null || !finishRecentsToAppThen(navStubView, doAction::run)) {
                            doAction.run()
                        }
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
                            val doAction = Runnable { CommonGesture.doAction(application, actionSingleTap) }
                            val navStubView = thisObject.navStubView
                            if (actionSingleTap !in foregroundAppRelatedActions || navStubView == null || !finishRecentsToAppThen(navStubView, doAction::run)) {
                                doAction.run()
                            }
                            result(true)
                        } else {
                            result(proceed())
                        }
                    }
                }
            }
        }
    }

    private fun finishRecentsToAppThen(navStubView: View, action: () -> Unit): Boolean {
        return runCatching {
            fldRecentsAnimationListenerImpl?.get(navStubView)?.let {
                metFinishController?.invoke(it, false, Runnable { navStubView.postDelayed(action, 48L) }, false)
                true
            } ?: false
        }.getOrDefault(false)
    }
}