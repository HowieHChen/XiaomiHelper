package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.aiengine.CopyWebsite

object AIEngine : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(CopyWebsite)
    }
}