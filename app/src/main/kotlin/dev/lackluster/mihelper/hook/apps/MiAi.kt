package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.miai.CustomSearch
import dev.lackluster.mihelper.hook.rules.miai.HideWatermark
import dev.lackluster.mihelper.utils.DexKit

object MiAi : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(CustomSearch)
        loadHooker(HideWatermark)
        DexKit.closeDexKit()
    }
}