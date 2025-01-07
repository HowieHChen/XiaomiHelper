@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.ACTION_HOME
import dev.lackluster.mihelper.data.Constants.ACTION_NOTIFICATIONS
import dev.lackluster.mihelper.data.Constants.ACTION_QUICK_SETTINGS
import dev.lackluster.mihelper.data.Constants.ACTION_RECENTS
import dev.lackluster.mihelper.data.Constants.ACTION_SCREENSHOT
import dev.lackluster.mihelper.data.Constants.PER_MIUI_INTERNAL_API
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object QuickSwitch : YukiBaseHooker() {
    /*
    0 -> PREVIOUS_APP;
    1 -> SYSTEM_ACTION_NOTIFICATIONS;
    2 -> SYSTEM_ACTION_QUICK_SETTINGS;
    3 -> SYSTEM_ACTION_LOCK_SCREEN;
    4 -> CAPTURE_SCREENSHOT;
    5 -> SYSTEM_ACTION_HOME;
    6 -> SYSTEM_ACTION_RECENTS;
    7 -> MI_AI_SCREEN;
    8 -> MI_AI_WAKE_UP;
     */
    private val actionQuickSwitchLeft = Prefs.getInt(Pref.Key.MiuiHome.QUICK_SWITCH_LEFT, 0)
    private val actionQuickSwitchRight = Prefs.getInt(Pref.Key.MiuiHome.QUICK_SWITCH_RIGHT, 0)
    private val backGestureHaptic = Prefs.getBoolean(Pref.Key.MiuiHome.BACK_HAPTIC, false)
    private var fakeTaskLeft: Any? = null
    private var fakeTaskRight: Any? = null
    private val applicationGetInstanceMethod by lazy {
        "com.miui.home.launcher.Application".toClass().method {
            name = "getInstance"
            modifiers { isStatic }
        }.get()
    }
    private val gestureBackArrowViewClass by lazy {
        "com.miui.home.recents.GestureBackArrowView".toClass()
    }
    private val gestureStubViewClass by lazy {
        "com.miui.home.recents.GestureStubView".toClass()
    }
    private val recentsModelClass by lazy {
        "com.miui.home.recents.RecentsModel".toClass()
    }
    private val taskConstructor by lazy {
        "com.android.systemui.shared.recents.model.Task".toClass().constructor {
            paramCount = 0
        }.give()
    }
    private val activityManagerWrapperClass by lazy {
        "com.android.systemui.shared.recents.system.ActivityManagerWrapper".toClass()
    }
    private val activityManagerClass by lazy {
        "android.app.ActivityManagerNative".toClass()
    }
    private val getNextTaskMethod by lazy {
        gestureStubViewClass.method {
            name = "getNextTask"
            param(ContextClass, BooleanType, IntType)
            modifiers { isStatic }
        }
    }
    private val setLaunchWindowingModeMethod by lazy {
        ActivityOptions::class.java.method {
            name = "setLaunchWindowingMode"
            param(IntType)
        }.give()
    }
    private val getHapticInstanceMethod by lazy {
        "com.miui.home.launcher.common.HapticFeedbackCompat".toClassOrNull()?.method {
            name = "getInstance"
            modifiers { isStatic }
        }?.get()
    }
    private val performGestureBackHandUpMethod by lazy {
        "com.miui.home.launcher.common.HapticFeedbackCompatV2".toClassOrNull()?.method {
            name = "performGestureBackHandUp"
            paramCount = 0
        }?.give()
    }
    private val readyStateRecent by lazy {
        "com.miui.home.recents.GestureBackArrowView\$ReadyState".toClass().enumConstants[2]
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.QUICK_SWITCH) {
            "com.miui.home.recents.GestureStubView\$3".toClass().method {
                name = "onSwipeStop"
            }.hook {
                before {
                    if (this.args(0).boolean()) {
                        val mGestureStubView = XposedHelpers.getObjectField(this.instance, "this\$0")
                        val mGestureBackArrowView = XposedHelpers.getObjectField(mGestureStubView, "mGestureBackArrowView")
                        val getCurrentState = XposedHelpers.callMethod(mGestureBackArrowView, "getCurrentState")
                        if (getCurrentState == readyStateRecent) {
                            XposedHelpers.callMethod(mGestureStubView, "onBackCancelled")
                            if (backGestureHaptic) {
                                val hapticInstance = getHapticInstanceMethod?.call()
                                performGestureBackHandUpMethod?.invoke(hapticInstance)
                            }
                        }
                    }
                }
            }
            gestureBackArrowViewClass.method {
                name = "loadRecentTaskIcon"
            }.hook {
                replaceAny {
                    val mNoneTaskIcon = this.instance.current().field {
                        name = "mNoneTaskIcon"
                    }.cast<Drawable?>()
                    val context = this.instance<View>().context ?: return@replaceAny mNoneTaskIcon
                    val mPosition = this.instance.current().field {
                        name = "mPosition"
                    }.int()
                    val icon = getNextTaskMethod.get().call(context, false, mPosition)?.current()
                        ?.field {
                            name = "icon"
                        }?.cast<Drawable?>()
                    return@replaceAny icon ?: mNoneTaskIcon
                }
            }
            gestureStubViewClass.apply {
                method {
                    name = "isDisableQuickSwitch"
                }.hook {
                    replaceToFalse()
                }
                getNextTaskMethod.hook {
                    before {
                        val context = this.args(0).cast<Context>() ?: return@before
                        val switch = this.args(1).boolean()
                        val targetPosition = this.args(2).int() // 0 -> left; 1 -> right; -1 -> unspecified
                        val position = if (targetPosition == 0 || targetPosition == 1) {
                            targetPosition
                        } else {
                            this.instance.current().field {
                                name = "mGestureStubPos"
                            }.int()
                        }
                        val isLeft = (position == 0)
                        val targetFunction = if (isLeft) actionQuickSwitchLeft else actionQuickSwitchRight
                        if (targetFunction != 0) {
                            this.result =
                                (if (isLeft) fakeTaskLeft else fakeTaskRight) ?: taskConstructor?.newInstance()?.apply {
                                    val iconResId = when(targetFunction) {
                                        1 -> R.drawable.ic_quick_switch_notifications
                                        2 -> R.drawable.ic_quick_switch_quick_settings
                                        3 -> R.drawable.ic_quick_switch_lock_screen
                                        4 -> R.drawable.ic_quick_switch_screenshot
                                        5 -> R.drawable.ic_quick_switch_home
                                        6 -> R.drawable.ic_quick_switch_recents
                                        7 -> R.drawable.ic_quick_switch_recognize_screen
                                        8 -> R.drawable.ic_quick_switch_xiaoai
                                        else -> R.drawable.ic_quick_switch_empty
                                    }
                                    val moduleIcon = Icon.createWithResource(BuildConfig.APPLICATION_ID, iconResId).loadDrawable(context)
                                    current().field {
                                        name = "icon"
                                    }.set(moduleIcon)
                                    if (isLeft) {
                                        fakeTaskLeft = this
                                    } else {
                                        fakeTaskRight = this
                                    }
                                }
                            if (switch) {
                                when (targetFunction) {
                                    3 -> {
                                        context.sendBroadcast(
                                            Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE")
                                                .putExtra("com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID", 10)
                                        )
                                    }
                                    4 -> {
                                        context.sendBroadcast(Intent(ACTION_SCREENSHOT))
                                    }
                                    7,8 -> {
                                        applicationGetInstanceMethod.invoke<Application>()?.startForegroundService(
                                            Intent("android.intent.action.ASSIST").apply {
                                                setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.VoiceService")
                                                putExtra(
                                                    "voice_assist_start_from_key",
                                                    if (targetFunction == 7) "long_press_fullscreen_gesture_line"
                                                    else "double_click_fullscreen_gesture_line"
                                                )
                                            }
                                        )
                                    }
                                    else -> {
                                        val intent = when(targetFunction) {
                                            1 -> Intent(ACTION_NOTIFICATIONS)
                                            2 -> Intent(ACTION_QUICK_SETTINGS)
                                            5 -> Intent(ACTION_HOME)
                                            6 -> Intent(ACTION_RECENTS)
                                            else -> return@before
                                        }
                                        context.sendBroadcast(intent, PER_MIUI_INTERNAL_API)
                                    }
                                }
                            }
                            return@before
                        }
                        val recentsModel = XposedHelpers.callStaticMethod(recentsModelClass, "getInstance", context)
                        val taskLoader = XposedHelpers.callMethod(recentsModel, "getTaskLoader")
                        val taskLoadPlan = XposedHelpers.callMethod(taskLoader, "createLoadPlan", context)
                        XposedHelpers.callMethod(taskLoader, "preloadTasks", taskLoadPlan, -1)
                        val taskStack = XposedHelpers.callMethod(taskLoadPlan, "getTaskStack")
                        var runningTask: ActivityManager.RunningTaskInfo? = null
                        // var activityOptions: ActivityOptions? = null
                        if (
                            taskStack == null ||
                            XposedHelpers.callMethod(taskStack, "getTaskCount") as Int == 0 ||
                            (XposedHelpers.callMethod(recentsModel, "getRunningTask" ) as ActivityManager.RunningTaskInfo?)?.also {
                                runningTask = it
                            } == null
                        ) {
                            this.result = null
                            return@before
                        }
                        val stackTasks = XposedHelpers.callMethod(taskStack, "getStackTasks") as ArrayList<*>
                        val size = stackTasks.size
                        var task: Any? = null
                        for (index in stackTasks.indices) {
                            if (XposedHelpers.getObjectField(
                                    XposedHelpers.getObjectField(stackTasks[index], "key"),
                                    "id"
                                ) as Int == runningTask!!.id) {
                                task = stackTasks[index + 1]
                                break
                            }
                        }
                        if (task == null && size >= 1 && "com.miui.home" == runningTask!!.baseActivity!!.packageName) {
                            task = stackTasks[0]
                        }

                        if (task != null && XposedHelpers.getObjectField(task, "icon") == null) {
                            XposedHelpers.setObjectField(
                                task,
                                "icon",
                                XposedHelpers.callMethod(
                                    taskLoader, "getAndUpdateActivityIcon",
                                    XposedHelpers.getObjectField(task, "key"),
                                    XposedHelpers.getObjectField(task, "taskDescription"),
                                    context.resources, true
                                )
                            )
                        }
                        if (!switch || task == null) {
                            this.result = task
                            return@before
                        }
                        val activityManagerWrapper = XposedHelpers.getStaticObjectField(
                            activityManagerWrapperClass,
                            "sInstance"
                        )
                        if (activityManagerWrapper == null) {
                            this.result = task
                            return@before
                        }
                        val key = XposedHelpers.getObjectField(task, "key")
                        val taskId = XposedHelpers.getObjectField(key, "id")
                        val windowingMode = XposedHelpers.getObjectField(key, "windowingMode") as Int
                        val activityOptions = ActivityOptions.makeBasic()
                        if (windowingMode == 3) {
                            setLaunchWindowingModeMethod?.invoke(activityOptions, 4)
                        }
                        try {
                            val clz = "com.android.systemui.shared.recents.utilities.RemoteAnimationFinishCallbackManager".toClass()
                            val remoteAnimationFinishCallbackManager = XposedHelpers.callStaticMethod(clz, "getInstance")
                            if (XposedHelpers.callMethod(remoteAnimationFinishCallbackManager, "isQuickSwitchApp") as? Boolean == true) {
                                XposedHelpers.callMethod(remoteAnimationFinishCallbackManager, "setQuickSwitchApp", false)
                                XposedHelpers.callMethod(remoteAnimationFinishCallbackManager, "setOpenTaskId", taskId)
                            }
                            if (XposedHelpers.callMethod(remoteAnimationFinishCallbackManager, "isQuickSwitchApp") as? Boolean == true) {
                                XposedHelpers.callMethod(remoteAnimationFinishCallbackManager, "finishMergeCallback")
                            }
                            XposedHelpers.callMethod(remoteAnimationFinishCallbackManager, "directExecuteWorkHandlerFinishRunnableIfNeed")
                            val activityManager = XposedHelpers.callStaticMethod(activityManagerClass, "getDefault")
                            XposedHelpers.callMethod(
                                activityManager,
                                "startActivityFromRecents",
                                taskId, activityOptions.toBundle()
                            )
                            this.result = task
                        } catch (t: Throwable) {
                            YLog.error("$t")
                            this.result = task
                        }
                    }
                }
            }
        }
    }
}