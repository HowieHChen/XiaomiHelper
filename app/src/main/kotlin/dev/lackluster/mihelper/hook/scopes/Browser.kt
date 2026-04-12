package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.browser.Configuration
import dev.lackluster.mihelper.hook.rules.browser.DebugMode
import dev.lackluster.mihelper.hook.rules.browser.BlockDialog
import dev.lackluster.mihelper.hook.rules.browser.DisableUpdateCheck
import dev.lackluster.mihelper.hook.rules.browser.HideHomepageTopBar
import dev.lackluster.mihelper.hook.rules.browser.SkipSplash
import dev.lackluster.mihelper.hook.rules.browser.SwitchEnv

object Browser : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(DebugMode)
        attach(SwitchEnv)
        attach(DisableUpdateCheck)
        attach(SkipSplash)
        attach(Configuration)
        attach(BlockDialog)
        attach(HideHomepageTopBar)
    }
}