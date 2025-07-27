package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object RemoveHotSeatNumLimit : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.DOCK_REMOVE_NUM_LIMIT) {
            "com.miui.home.launcher.DeviceConfig".toClassOrNull()?.apply {
                method {
                    name = "getHotseatMaxCount"
                }.hook {
                    replaceTo(99)
                }
            }
        }
    }
}