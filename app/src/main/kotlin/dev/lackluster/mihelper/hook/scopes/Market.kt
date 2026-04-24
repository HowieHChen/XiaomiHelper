package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.market.AdBlocker
import dev.lackluster.mihelper.hook.rules.market.BlockUpdateDialog
import dev.lackluster.mihelper.hook.rules.market.DisableCustomizeIcon
import dev.lackluster.mihelper.hook.rules.market.HideAppSecurity
import dev.lackluster.mihelper.hook.rules.market.HideTabItem
import dev.lackluster.mihelper.hook.rules.market.SkipSplash

object Market : StaticHooker() {
    override fun onInit() {
        attach(AdBlocker)
        attach(BlockUpdateDialog)
        attach(DisableCustomizeIcon)
        attach(HideAppSecurity)
        attach(HideTabItem)
        attach(SkipSplash)
    }
}