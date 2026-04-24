package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.remotecontroller.AdBlocker

object RemoteController : StaticHooker() {
    override fun onInit() {
        attach(AdBlocker)
    }
}