package dev.lackluster.mihelper.hook.rules.personalassist

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object BlockOriginalBlur : YukiBaseHooker() {
    private val ENABLED = Prefs.getBoolean(Pref.Key.MiuiHome.REFACTOR, false)
    private val MINUS_BLUR = Prefs.getBoolean(Pref.Key.MiuiHome.Refactor.MINUS_BLUR, Pref.DefValue.HomeRefactor.MINUS_BLUR)
    private val MINUS_DIM = Prefs.getBoolean(Pref.Key.MiuiHome.Refactor.MINUS_DIM, Pref.DefValue.HomeRefactor.MINUS_DIM)
    private val deviceBlurBlendAdapterClz by lazy {
        "com.miui.personalassistant.device.DeviceBlurBlendAdapter".toClassOrNull()
    }

    override fun onHook() {
        if (ENABLED && (MINUS_BLUR || MINUS_DIM)) {
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