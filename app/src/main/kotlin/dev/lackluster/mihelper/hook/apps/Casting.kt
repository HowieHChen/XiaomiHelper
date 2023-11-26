package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.AllowSendAllApp

object Casting : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AllowSendAllApp)
    }
}