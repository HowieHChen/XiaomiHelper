package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.shared.FuckAIVS

object FindDevice : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private val fuckAIVS by lazy { FuckAIVS(Preferences.FindDevice.FUCK_AIVS) }

    override fun onInit() {
        attach(fuckAIVS)
    }
}