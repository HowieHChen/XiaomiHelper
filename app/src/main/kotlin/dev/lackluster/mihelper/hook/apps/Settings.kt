package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.settings.ShowGoogle
import dev.lackluster.mihelper.hook.rules.settings.UnlockVoIPAssistant
import dev.lackluster.mihelper.hook.rules.shared.UnlockForPad

object Settings : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(ShowGoogle)
        loadHooker(UnlockForPad)
        loadHooker(UnlockVoIPAssistant)
    }
}