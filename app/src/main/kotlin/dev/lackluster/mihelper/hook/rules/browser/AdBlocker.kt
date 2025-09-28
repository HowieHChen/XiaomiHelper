package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object AdBlocker : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Browser.AD_BLOCKER) {
            "com.android.browser.DAUHotNewsHelper".toClassOrNull()?.apply {
                method {
                    name = "startHotNewsTipsDelayedTask"
                }.hook {
                    intercept()
                }
            }
        }
    }
}