package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.updater.DisableValidation

object Updater : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(DisableValidation)
    }
}