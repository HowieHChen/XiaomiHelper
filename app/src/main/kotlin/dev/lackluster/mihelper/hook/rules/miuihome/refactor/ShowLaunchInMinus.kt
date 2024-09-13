package dev.lackluster.mihelper.hook.rules.miuihome.refactor

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedBridge
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ShowLaunchInMinus : YukiBaseHooker() {
    // 0 -> Default; 1 -> Visible; 2 -> Overlap;
    private val MINUS_MODE = Prefs.getInt(Pref.Key.MiuiHome.Refactor.MINUS_MODE, 0)

    override fun onHook() {
        if (MINUS_MODE == 2) {
            "com.miui.home.launcher.overlay.assistant.AssistantDeviceAdapter".toClass().apply {
                method {
                    name = "inOverlapMode"
                }.hook {
                    replaceToTrue()
                }
            }
        } else {
            "com.miui.home.launcher.overlay.OverlayTransitionController".toClass().apply {
                method {
                    name = "onScrollChanged"
                }.hook {
                    replaceUnit {
                        val mCurrentAnimation = this.instance.current()
                            .field { name = "mCurrentAnimation"; superClass() }.any()
                            ?: return@replaceUnit
                        mCurrentAnimation.current().method {
                            name = "setPlayFraction"
                        }.call(
                            if (this.instance.current().field { name = "isTargetOpenOverlay"; superClass() }.boolean()) {
                                this.args(0).float()
                            } else {
                                1.0f - this.args(0).float()
                            }
                        )
                    }
                }
            }
            if (MINUS_MODE == 1) {
                val superGetSearchBarProperty =
                    "com.miui.home.launcher.LauncherState".toClass().method {
                        name = "getSearchBarProperty"
                    }.give() ?: return
                superGetSearchBarProperty.hook {
                    before {
                        // Make the original method accessible to avoid infinite loops
                    }
                }
                "com.miui.home.launcher.overlay.assistant.AssistantOverlayState".toClass().apply {
                    method {
                        name = "getVisibleElements"
                    }.hook {
                        replaceTo(1)
                    }
                    method {
                        name = "getSearchBarProperty"
                    }.hook {
                        before {
                            val superResult = XposedBridge.invokeOriginalMethod(
                                superGetSearchBarProperty,
                                this.instance,
                                this.args
                            ) as FloatArray
                            superResult[4] = this.instance.current().method { name = "getWorkspaceTranslationX" }.float(this.args[0])
                            this.result = superResult
                        }
                    }
                }
            }
        }
    }
}