package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DisableFakeNavBar : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_DISABLE_FAKE_NAVBAR) {
            "com.miui.home.recents.views.RecentsContainer".toClass()
                .method {
                    name = "hideFakeNavBarForHidingGestureLine"
                }
                .hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
        }
    }
}