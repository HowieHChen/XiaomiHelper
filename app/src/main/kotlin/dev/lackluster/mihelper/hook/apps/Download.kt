package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.download.FuckXL

object Download : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(FuckXL)
    }
}