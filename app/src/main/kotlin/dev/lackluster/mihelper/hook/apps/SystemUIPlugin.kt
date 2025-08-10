package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.systemui.plugin.AutoFlashlightOn

object SystemUIPlugin : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AutoFlashlightOn)
    }
}