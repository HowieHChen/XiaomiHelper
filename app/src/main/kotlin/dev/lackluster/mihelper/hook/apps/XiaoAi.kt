package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.xiaoai.CustomSearch
import dev.lackluster.mihelper.hook.rules.xiaoai.HideWatermark
import dev.lackluster.mihelper.utils.DexKit

object XiaoAi : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(CustomSearch)
        loadHooker(HideWatermark)
        DexKit.closeDexKit()
    }
}