package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DisableFixedOrientation : YukiBaseHooker() {
    private val shouldDisableFixedOrientationList by lazy {
        Prefs.getStringSet(PrefKey.ANDROID_NO_FIXED_ORIENTATION_LIST, mutableSetOf())
    }
    override fun onHook() {
        hasEnable(PrefKey.ANDROID_NO_FIXED_ORIENTATION) {
            "com.android.server.wm.MiuiFixedOrientationController".toClass()
                .method {
                    name = "shouldDisableFixedOrientation"
                }
                .hook {
                    before {
                        if (this.args(0).string() in shouldDisableFixedOrientationList) {
                            this.result = true
                        }
                    }
                }
        }
    }
}