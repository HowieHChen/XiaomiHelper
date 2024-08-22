package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.mitrust.DisableRiskCheck

object MiTrust : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(DisableRiskCheck)
    }
}