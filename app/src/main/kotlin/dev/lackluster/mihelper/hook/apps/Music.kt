package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.music.AdBlocker

object Music : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AdBlocker)
    }
}