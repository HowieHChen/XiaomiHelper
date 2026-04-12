package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get

object DisableWakePathChecker : StaticHooker() {
    private val metGetCheckStartActivityIntent by lazy {
        "miui.app.ActivitySecurityHelper".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getCheckStartActivityIntent"
        }
    }

    override fun onInit() {
        updateSelfState(Preferences.SecurityCenter.LINK_START.get() == 2)
    }

    override fun onHook() {
        metGetCheckStartActivityIntent?.hook { result(null) }
    }
}