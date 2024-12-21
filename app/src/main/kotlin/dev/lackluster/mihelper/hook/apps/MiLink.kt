package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.milink.ContinueTasks
import dev.lackluster.mihelper.hook.rules.milink.EnhanceContinueTasks
import dev.lackluster.mihelper.hook.rules.milink.FuckHpplay
import dev.lackluster.mihelper.utils.DexKit

object MiLink : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(FuckHpplay)
        loadHooker(ContinueTasks)
        loadHooker(EnhanceContinueTasks)
        DexKit.closeDexKit()
    }
}