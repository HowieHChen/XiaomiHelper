package dev.lackluster.mihelper.hook.rules.incallui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object HideCRBT : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.INCALLUI_HIDE_CRBT) {
            "com.android.incallui.Call".toClass()
                .method {
                    name = "setPlayingVideoCrbt"
                }
                .hook {
                    before {
                        this.args(0).set(0)
                        this.args(1).setFalse()
                    }
                }
        }
    }
}