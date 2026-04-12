package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.downloadui.HideXL

object DownloadUI : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(HideXL)
    }
}