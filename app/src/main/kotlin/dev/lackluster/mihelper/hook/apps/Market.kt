package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.market.AdBlocker
import dev.lackluster.mihelper.hook.rules.market.BlockUpdateDialog
import dev.lackluster.mihelper.hook.rules.market.HideTabItem
import dev.lackluster.mihelper.hook.rules.market.SkipSplash

object Market : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AdBlocker)
        loadHooker(SkipSplash)
        loadHooker(HideTabItem)
        loadHooker(BlockUpdateDialog)
    }
}