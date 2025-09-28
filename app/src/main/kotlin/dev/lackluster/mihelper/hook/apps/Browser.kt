package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.browser.AdBlocker
import dev.lackluster.mihelper.hook.rules.browser.DebugMode
import dev.lackluster.mihelper.hook.rules.browser.DisableUpdateCheck
import dev.lackluster.mihelper.hook.rules.browser.HideAiSearchEntry
import dev.lackluster.mihelper.hook.rules.browser.HideHomepageTopBar
import dev.lackluster.mihelper.hook.rules.browser.RemoveAppRec
import dev.lackluster.mihelper.hook.rules.browser.SkipSplash
import dev.lackluster.mihelper.hook.rules.browser.SwitchEnv
import dev.lackluster.mihelper.utils.DexKit

object Browser : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AdBlocker)
        loadHooker(DebugMode)
        loadHooker(SwitchEnv)
        loadHooker(DisableUpdateCheck)
        loadHooker(SkipSplash)
        loadHooker(RemoveAppRec)
        loadHooker(HideHomepageTopBar)
        loadHooker(HideAiSearchEntry)
        DexKit.closeDexKit()
    }
}