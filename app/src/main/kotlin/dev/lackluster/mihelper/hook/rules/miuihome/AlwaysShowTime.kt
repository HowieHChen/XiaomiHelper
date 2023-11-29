package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object AlwaysShowTime : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_ALWAYS_SHOW_TIME) {
            try {
                "com.miui.home.launcher.Workspace".toClass()
                    .method {
                        name = "isScreenHasClockGadget"
                    }
                    .ignored()
                    .onNoSuchMethod {
                        throw it
                    }
            }
            catch (_: Throwable) {
                "com.miui.home.launcher.Workspace".toClass()
                    .method {
                        name = "isScreenHasClockWidget"
                    }
                    .ignored()
                    .onNoSuchMethod {
                        throw it
                    }
            }
            catch (_: Throwable) {
                "com.miui.home.launcher.Workspace".toClass()
                    .method {
                        name = "isClockWidget"
                    }
            }.hook {
                replaceToFalse()
            }
        }
    }
}