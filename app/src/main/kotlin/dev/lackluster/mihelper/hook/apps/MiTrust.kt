package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.mitrust.DisableRiskCheck
import dev.lackluster.mihelper.utils.DexKit

object MiTrust : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(DisableRiskCheck)
        DexKit.closeDexKit()
    }
}