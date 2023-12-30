package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object PadShowMemory : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_PAD_SHOW_MEMORY, extraCondition = { Device.isPad }) {
            "com.miui.home.recents.views.RecentsDecorations".toClass().apply {
                method {
                    name = "hideTxtMemoryInfoView"
                }.ignored().hook {
                    intercept()
                }
                method {
                    name = "isMemInfoShow"
                }.ignored().hook {
                    replaceToTrue()
                }
            }
        }
    }
}