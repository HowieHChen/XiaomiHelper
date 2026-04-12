package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.incallui.HideCRBT

object InCallUI : StaticHooker() {
    override fun onInit() {
        attach(HideCRBT)
    }
}