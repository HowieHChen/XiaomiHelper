package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.mitrust.DisableRiskCheck

object MiTrust : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(DisableRiskCheck)
    }
}