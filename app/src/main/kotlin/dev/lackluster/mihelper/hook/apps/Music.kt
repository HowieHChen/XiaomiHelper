package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.music.AdBlock

object Music : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AdBlock)
    }
}