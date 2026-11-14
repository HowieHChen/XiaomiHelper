package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.taplus.CustomSearch
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.taplus.XiaoAiIntegration

object Taplus : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override fun onInit() {
        attach(ResourcesUtils)
        attach(CustomSearch)
        attach(XiaoAiIntegration)
    }
}
