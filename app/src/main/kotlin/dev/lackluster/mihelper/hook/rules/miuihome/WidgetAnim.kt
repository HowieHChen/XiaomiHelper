package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object WidgetAnim : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_WIDGET_ANIM) {
            "com.miui.home.launcher.LauncherWidgetView".toClass()
                .method {
                    name = "isUseTransitionAnimation"
                }
                .hook {
                    replaceToTrue()
                }
            "com.miui.home.launcher.maml.MaMlWidgetView".toClass()
                .method {
                    name = "isUseTransitionAnimation"
                }
                .hook {
                    replaceToTrue()
                }
        }
    }
}