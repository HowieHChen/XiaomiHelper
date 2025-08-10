package dev.lackluster.mihelper.hook.rules.systemui.plugin

import android.app.Activity
import android.view.View
import androidx.core.view.postDelayed
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AutoFlashlightOn : YukiBaseHooker() {
    private val operateFlashlight by lazy {
        "miui.systemui.flashlight.MiFlashlightManager".toClassOrNull()?.method {
            name = "operate"
            param(BooleanType)
        }?.give()
    }

    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.Plugin.AUTO_FLASH_ON, extraCondition = { operateFlashlight != null}) {
            "miui.systemui.flashlight.MiFlashlightActivity".toClassOrNull()?.apply {
                method {
                    name = "onResume"
                    superClass()
                }.hook {
                    after {
                        val activity = this.instance<Activity>()
                        val fromKeyguard = activity.intent.getBooleanExtra("from_keyguard_shortcut", false)
                        if (fromKeyguard) {
                            val flashlightManager = this.instance.current().field {
                                name = "flashlightManager"
                            }.any() ?: return@after
                            val miFlashlightLayout = this.instance.current().method {
                                name = "getFlashlightLayout"
                            }.invoke<View>() ?: return@after
                            miFlashlightLayout.postDelayed(700) {
                                operateFlashlight?.invoke(flashlightManager, true)
                            }
                        }
                    }
                }
            }
        }
    }
}