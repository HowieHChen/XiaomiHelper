package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.milink.ContinueTasks
import dev.lackluster.mihelper.hook.rules.milink.FuckHpplay
import dev.lackluster.mihelper.hook.rules.shared.FuckAIVS

object MiLink : StaticHooker() {
    private val fuckAIVS by lazy { FuckAIVS(Preferences.MiLink.FUCK_HPPLAY) }

    override fun onInit() {
        attach(FuckHpplay)
        attach(ContinueTasks)
        attach(fuckAIVS)
    }
}