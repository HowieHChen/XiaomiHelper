package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.settings.FontScale
import dev.lackluster.mihelper.hook.rules.settings.HeaderList
import dev.lackluster.mihelper.hook.rules.settings.QuickPermission

object Settings : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override fun onInit() {
        attach(FontScale)
        attach(HeaderList)
        attach(QuickPermission)
    }
}