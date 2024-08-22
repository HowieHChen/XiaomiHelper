package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.market.AdBlocker

object Market : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AdBlocker)
    }
}