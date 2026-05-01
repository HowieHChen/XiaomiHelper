package dev.lackluster.mihelper.hook.rules.intentresolver

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet

object SharePanelStyle : StaticHooker() {
    private val style by Preferences.MiIntentResolver.SHARE_PANEL_STYLE.lazyGet()

    override fun onInit() {
        updateSelfState(style != 0)
    }

    override fun onHook() {
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