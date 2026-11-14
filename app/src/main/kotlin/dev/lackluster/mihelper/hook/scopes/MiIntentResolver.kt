package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.intentresolver.RerankTargets
import dev.lackluster.mihelper.hook.rules.intentresolver.SharePanelStyle

object MiIntentResolver : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override fun onInit() {
        attach(SharePanelStyle)
        attach(RerankTargets)
    }
}