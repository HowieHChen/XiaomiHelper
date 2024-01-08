package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Math
import dev.lackluster.mihelper.utils.Prefs

object BlurRadius : YukiBaseHooker() {
    private const val blurDefValue = 100
    private val blurRadius = Prefs.getInt(PrefKey.HOME_BLUR_RADIUS, blurDefValue)
    override fun onHook() {
        if (blurRadius != blurDefValue) {
            "com.miui.launcher.utils.BlurUtils".toClass()
                .method {
                    name = "blurRadiusOfRatio"
                    paramCount = 1
                    modifiers { isStatic }
                }
                .hook {
                    replaceAny {
                        if (this.args(0).float() == 0.0f) {
                            0
                        }
                        else {
                            Math.linearInterpolate(1, blurRadius, this.args(0).float())
                        }
                    }
                }
        }
    }
}