package dev.lackluster.mihelper.hook.rules.android

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object DisableWakePathChecker : YukiBaseHooker() {
    override fun onHook() {
        if (Prefs.getInt(Pref.Key.SecurityCenter.LINK_START, 0) == 2) {
            "miui.app.ActivitySecurityHelper".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "getCheckStartActivityIntent"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}