package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.taplus.CustomSearch

object Taplus : StaticHooker() {
    override fun onInit() {
        attach(CustomSearch)
    }
}