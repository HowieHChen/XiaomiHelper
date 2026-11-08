package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object DisableWakePathChecker : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private val metGetCheckStartActivityIntent by lazy {
        "miui.app.ActivitySecurityHelper".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getCheckStartActivityIntent"
        }
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.SecurityCenter.LINK_START.observe {
                updateSelfState(it == 2)
            }
        )
    }

    override fun onStart() {
        metGetCheckStartActivityIntent?.hook { result(null) }
    }
}