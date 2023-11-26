package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.mishare.NoAutoOff
import dev.lackluster.mihelper.utils.DexKit

object MiShare : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(NoAutoOff)
        DexKit.closeDexKit()
    }
}