package dev.lackluster.mihelper.hook.rules.intentresolver

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object SharePanelStyle : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    @Volatile
    private var style = 0

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.MiIntentResolver.SHARE_PANEL_STYLE.observe {
                style = it
                updateSelfState(it != 0)
            }
        )
    }

    override fun onStart() {
        "com.android.intentresolver.ApplicationStubImpl".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "useAospVersion"
            }?.hook {
                when (style) {
                    1 -> result(true)
                    2 -> result(false)
                    else -> result(proceed())
                }
            }
        }
    }
}