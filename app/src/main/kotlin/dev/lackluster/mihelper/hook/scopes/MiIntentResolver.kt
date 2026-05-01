package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.intentresolver.RerankTargets

object MiIntentResolver : StaticHooker() {
    override fun onInit() {
        attach(RerankTargets)
    }
}