package dev.lackluster.mihelper.hook.rules.personalassist

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object BlockOriginalBlur : YukiBaseHooker() {
    private val ENABLED = Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
    private val BLUR_TYPE = Prefs.getInt(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, 0)
    private val deviceBlurBlendAdapterClz by lazy {
        "com.miui.personalassistant.device.DeviceBlurBlendAdapter".toClassOrNull()
    }

    override fun onHook() {
        if (ENABLED || BLUR_TYPE == 2) {
            deviceBlurBlendAdapterClz?.apply {
                // Lcom/miui/personalassistant/device/DeviceBlurBlendAdapter;->onScrollProgressChanged(F)V
                method {
                    name = "onScrollProgressChanged"
                    paramCount = 1
                }.hook {
                    intercept()
                }
            }
        }
    }
}