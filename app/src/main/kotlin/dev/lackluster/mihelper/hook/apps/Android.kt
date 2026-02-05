package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.android.AllowMoreFreeform
import dev.lackluster.mihelper.hook.rules.android.DarkModeForAll
import dev.lackluster.mihelper.hook.rules.android.DisableFixedOrientation
import dev.lackluster.mihelper.hook.rules.android.DisableWakePathChecker
import dev.lackluster.mihelper.hook.rules.android.FontScale
import dev.lackluster.mihelper.hook.rules.android.RemoveFreeformRestriction

object Android : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(DarkModeForAll)
        loadHooker(RemoveFreeformRestriction)
        loadHooker(AllowMoreFreeform)
        loadHooker(FontScale)
        loadHooker(DisableWakePathChecker)
        return
        loadHooker(DisableFixedOrientation)
    }
}