package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.plugin.AutoFlashlightOn

object SystemUIPlugin : StaticHooker() {
    override fun onInit() {
        attach(AutoFlashlightOn)
    }
}