package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.milink.FuckHpplay
import dev.lackluster.mihelper.hook.rules.shared.AllowSendAllApp

object MiLink : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(FuckHpplay)
        loadHooker(AllowSendAllApp)
    }
}