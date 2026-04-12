package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.milink.ContinueTasks
import dev.lackluster.mihelper.hook.rules.milink.FuckHpplay

object MiLink : StaticHooker() {
    override fun onInit() {
        attach(FuckHpplay)
        attach(ContinueTasks)
    }
}