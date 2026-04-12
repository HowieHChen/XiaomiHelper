package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.miai.CustomSearch
import dev.lackluster.mihelper.hook.rules.miai.HideWatermark

object MiAi : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(CustomSearch)
        attach(HideWatermark)
    }
}