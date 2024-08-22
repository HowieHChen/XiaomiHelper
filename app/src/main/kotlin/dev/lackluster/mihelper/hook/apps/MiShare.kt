package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.mishare.AlwaysOn
import dev.lackluster.mihelper.utils.DexKit

object MiShare : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AlwaysOn)
        DexKit.closeDexKit()
    }
}