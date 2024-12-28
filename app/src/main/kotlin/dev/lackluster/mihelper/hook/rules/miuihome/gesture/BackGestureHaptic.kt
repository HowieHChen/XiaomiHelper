package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object BackGestureHaptic : YukiBaseHooker() {
    private const val TIME_OUT_BLOCKER_KEY = "BLOCKER_ID_FOR_HAPTIC_GESTURE_BACK"
    private val hapticFeedbackV2Class by lazy {
        "com.miui.home.launcher.common.HapticFeedbackCompatV2".toClassOrNull()
    }
    private val gestureStubViewClass by lazy {
        "com.miui.home.recents.GestureStubView".toClass()
    }
    private val getHandlerMethod by lazy {
        "com.miui.home.launcher.common.BackgroundThread".toClass().method {
            name = "getHandler"
            modifiers { isStatic }
        }.get()
    }
    private val startCountDownMethod by lazy {
        "com.miui.home.recents.util.TimeOutBlocker".toClass().method {
            name = "startCountDown"
            modifiers { isStatic }
        }.get()
    }
    private val isBlockedMethod by lazy {
        "com.miui.home.recents.util.TimeOutBlocker".toClass().method {
            name = "isBlocked"
            modifiers { isStatic }
        }.get()
    }
    private val performExtHapticFeedbackMethod by lazy {
        "miuix.util.HapticFeedbackCompat".toClass().method {
            name = "performExtHapticFeedback"
            param(IntType)
        }.give()
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.BACK_HAPTIC) {
            if (hapticFeedbackV2Class == null) return@hasEnable
            hapticFeedbackV2Class?.apply {
                method {
                    name = "performGestureReadyBack"
                }.hook {
                    after {
                        val handler = getHandlerMethod.call()
                        startCountDownMethod.call(handler, 140L, TIME_OUT_BLOCKER_KEY)
                    }
                }
                method {
                    name = "performGestureBackHandUp"
                }.hook {
                    before {
                        val isBlocked = isBlockedMethod.call(TIME_OUT_BLOCKER_KEY) as? Boolean
                        if (isBlocked == true) {
                            this.result = null
                        }
                    }
                }
                method {
                    name = "lambda\$performGestureReadyBack\$11"
                }.remedys {
                    method {
                        name = "lambda\$performGestureReadyBack\$11\$HapticFeedbackCompatV2"
                    }
                }.hook {
                    replaceUnit {
                        val mHapticHelper = this.instance.current().field {
                            name = "mHapticHelper"
                        }.any()
                        performExtHapticFeedbackMethod?.invoke(mHapticHelper, 0)
                    }
                }
                method {
                    name = "lambda\$performGestureBackHandUp\$12"
                }.remedys {
                    method {
                        name = "lambda\$performGestureBackHandUp\$12\$HapticFeedbackCompatV2"
                    }
                }.hook {
                    replaceUnit {
                        val mHapticHelper = this.instance.current().field {
                            name = "mHapticHelper"
                        }.any()
                        performExtHapticFeedbackMethod?.invoke(mHapticHelper, 1)
                    }
                }
            }
            gestureStubViewClass.method {
                name = "injectKeyEvent"
                param(IntType, BooleanType)
            }.hook {
                before {
                    this.args(1).setTrue()
                }
            }
        }
    }
}