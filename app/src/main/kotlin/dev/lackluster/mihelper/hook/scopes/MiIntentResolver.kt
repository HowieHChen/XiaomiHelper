package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.intentresolver.RerankTargets
import dev.lackluster.mihelper.hook.rules.intentresolver.SharePanelStyle

object MiIntentResolver : StaticHooker() {
    override fun onInit() {
        attach(SharePanelStyle)
        attach(RerankTargets)
    }
}