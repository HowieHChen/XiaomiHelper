package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.settings.HeaderList
import dev.lackluster.mihelper.hook.rules.settings.QuickPermission
import dev.lackluster.mihelper.hook.rules.settings.UnlockTaplusForPad

object Settings : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(UnlockTaplusForPad)
        loadHooker(HeaderList)
        loadHooker(QuickPermission)
    }
}