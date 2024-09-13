package dev.lackluster.mihelper.hook.rules.miuihome.refactor

import android.view.ViewGroup
import android.widget.FrameLayout
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry.minusBlurView
import dev.lackluster.mihelper.utils.Prefs

object BlurMinus : YukiBaseHooker() {
//    private val MINUS_OVERLAP = Prefs.getBoolean(Pref.Key.MiuiHome.Refactor.MINUS_OVERLAP, Pref.DefValue.HomeRefactor.MINUS_OVERLAP)
    private val MINUS_MODE = Prefs.getInt(Pref.Key.MiuiHome.Refactor.MINUS_MODE, 0)
    private val SHOW_LAUNCH_IN_MINUS_SCALE = Prefs.getFloat(Pref.Key.MiuiHome.Refactor.SHOW_LAUNCH_IN_MINUS_SCALE, Pref.DefValue.HomeRefactor.SHOW_LAUNCH_IN_MINUS_SCALE)
    override fun onHook() {
        "com.miui.home.launcher.overlay.assistant.AssistantOverlayTransitionController".toClass().apply {
            // Lcom/miui/home/launcher/overlay/assistant/AssistantOverlayTransitionController;->onScrollChanged(F)V
            method {
                name = "onScrollChanged"
                paramCount = 1
            }.hook {
                before {
                    val targetRatio = this.args(0).float()
                    minusBlurView?.setStatus(targetRatio, false)
                    if (MINUS_MODE == 2) {
                        val mLauncher = this.instance.current().field {
                            name = "mLauncher"
                            superClass()
                        }.any()
                        val mScreenContent = XposedHelpers.getObjectField(mLauncher, "mScreenContent") as? FrameLayout ?: return@before
                        val scale = SHOW_LAUNCH_IN_MINUS_SCALE + (1 - targetRatio) * (1 - SHOW_LAUNCH_IN_MINUS_SCALE)
                        mScreenContent.scaleX = scale
                        mScreenContent.scaleY = scale
//                                mScreenContent.scaleX = 1.0f
//                                mScreenContent.scaleY = 1.0f
                        this.result = null
                    }
                }
            }
            // Lcom/miui/home/launcher/overlay/assistant/AssistantOverlayTransitionController;->onScrollEnd(F)V
            method {
                name = "onScrollEnd"
                paramCount = 1
            }.hook {
                after {
                    val targetRatio = this.args(0).float()
                    minusBlurView?.setStatus(targetRatio, false)
                    if (MINUS_MODE == 2) {
                        val mLauncher = this.instance.current().field {
                            name = "mLauncher"
                            superClass()
                        }.any()
                        val mWorkspace = XposedHelpers.getObjectField(
                            mLauncher,
                            "mWorkspace"
                        ) as? ViewGroup ?: return@after
                        mWorkspace.scaleX = 1.0f
                        mWorkspace.scaleY = 1.0f
                        val mScreenContent = XposedHelpers.getObjectField(mLauncher, "mScreenContent") as? FrameLayout ?: return@after
                        val scale = SHOW_LAUNCH_IN_MINUS_SCALE + (if (targetRatio == 1.0f) 0 else 1) * (1 - SHOW_LAUNCH_IN_MINUS_SCALE)
                        mScreenContent.scaleX = scale
                        mScreenContent.scaleY = scale
//                                mScreenContent.scaleX = 1.0f
//                                mScreenContent.scaleY = 1.0f
                    }
                }
            }
        }

    }
}