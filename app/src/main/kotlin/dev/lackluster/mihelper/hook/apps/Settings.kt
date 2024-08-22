package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.settings.HeaderList
import dev.lackluster.mihelper.hook.rules.settings.UnlockVoIPAssistant
import dev.lackluster.mihelper.hook.rules.shared.UnlockTaplusForPad

object Settings : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(UnlockTaplusForPad)
        loadHooker(UnlockVoIPAssistant)
        loadHooker(HeaderList)
    }
}