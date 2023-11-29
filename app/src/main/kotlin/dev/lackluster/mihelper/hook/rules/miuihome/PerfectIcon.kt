package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object PerfectIcon : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_ICON_PERFECT_ICON) {
            "com.miui.home.library.compat.LauncherActivityInfoCompat".toClass()
                .method {
                    name = "getIconResource"
                }
                .hook {
                    replaceTo(0)
                }
        }
    }
}