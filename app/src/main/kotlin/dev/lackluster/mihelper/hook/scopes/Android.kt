package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.android.AllowMoreFreeform
import dev.lackluster.mihelper.hook.rules.android.DarkModeForAll
import dev.lackluster.mihelper.hook.rules.android.DisableWakePathChecker
import dev.lackluster.mihelper.hook.rules.android.FontScale
import dev.lackluster.mihelper.hook.rules.android.RemoveFreeformRestriction

object Android : StaticHooker() {
    override fun onInit() {
        attach(DarkModeForAll)
        attach(RemoveFreeformRestriction)
        attach(AllowMoreFreeform)
        attach(FontScale)
        attach(DisableWakePathChecker)
    }
}