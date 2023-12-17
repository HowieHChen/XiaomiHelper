package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object RemoveFreeformRestriction : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.ANDROID_FREEFORM_RESTRICTION) {
            val activityTaskManagerService = "com.android.server.wm.ActivityTaskManagerService".toClass()
            val settingsObserver = "com.android.server.wm.WindowManagerService\$SettingsObserver".toClass()

            val wmTask = "com.android.server.wm.Task".toClass()
            val miuiMultiWindowAdapter = "android.util.MiuiMultiWindowAdapter".toClass()
            val miuiMultiWindowUtils = "android.util.MiuiMultiWindowUtils".toClass()

            for (methodName in setOf(
                "getFreeformBlackList",
                "getFreeformBlackListFromCloud",
                "getAbnormalFreeformBlackList",
                "getAbnormalFreeformBlackListFromCloud",
                "getStartFromFreeformBlackList",
                "getStartFromFreeformBlackListFromCloud",
                "getForegroundPinAppBlackList",
                "getForegroundPinAppBlackListFromCloud"
            )) {
                miuiMultiWindowAdapter.method {
                    name = methodName
                }.hookAll {
                    replaceTo(mutableListOf<String>())
                }
            }

            for (methodName in setOf(
                "isForceResizeable",
                "supportFreeform"
            )) {
                miuiMultiWindowUtils.method {
                    name = methodName
                }.hookAll {
                    replaceToTrue()
                }
            }

            miuiMultiWindowUtils.method {
                name = "initFreeFormResolutionArgsOfDevice"
            }.hookAll {
                before {
                    this.args(0).set("zizhan")
                }
            }

            wmTask.method {
                name = "isResizeable"
            }.hook {
                replaceToTrue()
            }


            activityTaskManagerService.method {
                name = "retrieveSettings"
            }.hookAll {
                after {
                    this.instance.current().field {
                        name = "mDevEnableNonResizableMultiWindow"
                    }.setTrue()
                }
            }

            settingsObserver.method {
                name = "updateDevEnableNonResizableMultiWindow"
            }.hookAll {
                after {
                    val this0 = this.instance.current().field {
                        name = "this\$0"
                    }.any() ?: return@after
                    val mAtmService = this0.current().field {
                        name = "mAtmService"
                    }.any() ?: return@after
                    mAtmService.current().field {
                        name = "mDevEnableNonResizableMultiWindow"
                    }.setTrue()
                }
            }

            settingsObserver.method {
                name = "onChange"
            }.hookAll {
                after {
                    val this0 = this.instance.current().field {
                        name = "this\$0"
                    }.any() ?: return@after
                    val mAtmService = this0.current().field {
                        name = "mAtmService"
                    }.any() ?: return@after
                    mAtmService.current().field {
                        name = "mDevEnableNonResizableMultiWindow"
                    }.setTrue()
                }
            }

            "com.android.server.wm.MiuiFreeFormStackDisplayStrategy".toClass()
                .method {
                    name = "getMaxMiuiFreeFormStackCount"
                }
                .hook {
                    replaceTo(256)
                }

            "miui.app.MiuiFreeFormManager".toClass()
                .method {
                    name = "getMaxMiuiFreeFormStackCountForFlashBack"
                }
                .hook {
                    replaceTo(256)
                }

            "com.android.server.wm.MiuiFreeFormGestureController".toClassOrNull()
                ?.method {
                    name = "needForegroundPin"
                }
                ?.hook {
                    replaceToTrue()
                }
        }
    }
}