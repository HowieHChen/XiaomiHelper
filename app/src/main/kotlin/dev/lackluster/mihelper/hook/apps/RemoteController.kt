package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.remotecontroller.AdBlocker

object RemoteController : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AdBlocker)
    }
}