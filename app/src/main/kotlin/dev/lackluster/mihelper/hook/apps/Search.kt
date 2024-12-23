package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.search.MoreSearchEngine
import dev.lackluster.mihelper.utils.DexKit

object Search : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(MoreSearchEngine)
        DexKit.closeDexKit()
    }
}