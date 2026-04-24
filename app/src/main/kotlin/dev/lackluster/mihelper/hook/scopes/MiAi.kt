package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.miai.CustomSearch
import dev.lackluster.mihelper.hook.rules.miai.HideWatermark
import dev.lackluster.mihelper.hook.rules.shared.FuckAIVS

object MiAi : StaticHooker() {
    private val fuckAIVS by lazy { FuckAIVS(Preferences.MiAi.FUCK_AIVS) }

    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(CustomSearch)
        attach(HideWatermark)
        attach(fuckAIVS)
    }
}