@file:Suppress("DEPRECATION")

package dev.lackluster.mihelper.hook.rules.miuihome.gesture

import android.app.ActivityManager
import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object QuickBack : YukiBaseHooker() {
//    private val applicationClz by lazy {
//        "com.miui.home.launcher.Application".toClass()
//    }
//    private val navStubViewClz by lazy {
//        "com.miui.home.recents.NavStubView"
//    }
    private val gestureStubViewClz by lazy {
        "com.miui.home.recents.GestureStubView".toClass()
    }
    private val recentsModelClz by lazy {
        "com.miui.home.recents.RecentsModel".toClass()
    }
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.QUICK_BACK) {
            gestureStubViewClz.method {
                name = "isDisableQuickSwitch"
            }.hook {
                replaceToFalse()
            }
//            "com.miui.home.recents.GestureBackArrowView".toClass().method {
//                name = "loadRecentTaskIcon"
//            }.hook {
//                before {
//                    val instance = this.instance
//                    val mNoneTaskIcon = XposedHelpers.getObjectField(instance, "mNoneTaskIcon") as Drawable
//                    val mKeyguardManager = XposedHelpers.getObjectField(instance, "mKeyguardManager") as KeyguardManager
//                    val mContentResolver = XposedHelpers.getObjectField(instance, "mContentResolver") as ContentResolver
//                    val supportNextTask = XposedHelpers.callStaticMethod(gestureStubViewClz, "supportNextTask", mKeyguardManager, mContentResolver) as Boolean
//                    if (!supportNextTask) {
//                        this.result = mNoneTaskIcon
//                        return@before
//                    }
//                    val position = if (XposedHelpers.getObjectField(instance, "mPosition") as Int == 0) 0 else 1 // 0 -> left; 1 -> right
//                    val nextTask = XposedHelpers.callStaticMethod(gestureStubViewClz, "getNextTask", (instance as View).context, false, position)
//                    if (nextTask != null) {
//                        val taskIcon = XposedHelpers.getObjectField(nextTask, "icon") as Drawable?
//                        if (taskIcon != null) {
//                            this.result = taskIcon
//                            return@before
//                        }
//                    }
//                    this.result = mNoneTaskIcon
//                }
//            }
            gestureStubViewClz.method {
                name = "getNextTask"
                param(ContextClass, BooleanType, IntType)
            }.hook {
                before {
                    val context = this.args(0).any() as? Context ?: return@before
                    val switch = this.args(1).boolean()
                    // val position = this.args(2).int() // 0 -> left; 1 -> right

                    val recentsModel = XposedHelpers.callStaticMethod(recentsModelClz, "getInstance", context)
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
//                    if (i == 0) {
//                        activityOptions = ActivityOptions.makeCustomAnimation(
//                            context,
////                            R.anim.recents_quick_switch_left_enter,
////                            R.anim.recents_quick_switch_left_exit
//                            ResourcesUtils.recents_quick_switch_left_enter,
//                            ResourcesUtils.recents_quick_switch_left_exit
//                        )
//                    } else if (i == 1) {
//                        activityOptions = ActivityOptions.makeCustomAnimation(
//                            context,
////                            R.anim.recents_quick_switch_right_enter,
////                            R.anim.recents_quick_switch_right_exit
//                            ResourcesUtils.recents_quick_switch_right_enter,
//                            ResourcesUtils.recents_quick_switch_right_exit
//                        )
//                    }
                    val activityManagerWrapper = XposedHelpers.getStaticObjectField(
                        "com.android.systemui.shared.recents.system.ActivityManagerWrapper".toClass(),
                        "sInstance"
                    )
                    if (activityManagerWrapper == null) {
                        this.result = task
                    }
                    try {
                        XposedHelpers.callMethod(
                            activityManagerWrapper, "startActivityFromRecents",
                            XposedHelpers.getObjectField(task, "key"),
                            null // activityOptions
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