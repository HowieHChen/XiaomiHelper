package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.download.FuckXL

object Download : StaticHooker() {
    override fun onInit() {
        attach(FuckXL)
    }
}