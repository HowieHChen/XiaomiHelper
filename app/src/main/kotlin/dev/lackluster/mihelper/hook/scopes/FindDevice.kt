package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.shared.FuckAIVS

object FindDevice : StaticHooker() {
    private val fuckAIVS by lazy { FuckAIVS(Preferences.FindDevice.FUCK_AIVS) }

    override fun onInit() {
        attach(fuckAIVS)
    }
}