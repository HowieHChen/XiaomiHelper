package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.mimirror.ContinueTasks
import dev.lackluster.mihelper.utils.DexKit

object MiMirror : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(ContinueTasks)
        DexKit.closeDexKit()
    }
}