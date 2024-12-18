package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.themes.SkipSplash
import dev.lackluster.mihelper.utils.DexKit

object Themes : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(SkipSplash)
        DexKit.closeDexKit()
    }
}