package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.utils.Prefs

object ForceColorScheme : YukiBaseHooker() {
    private val forceColorScheme = Prefs.getInt(Pref.Key.SystemUI.LockScreen.FORCE_COLOR_STATUS_BAR, 0)

    override fun onHook() {
        if (forceColorScheme == 0) return
        clzMiuiKeyguardStatusBarView?.apply {
            val fldLightLockScreenWallpaper = resolve().firstFieldOrNull {
                name = "mLightLockScreenWallpaper"
            }
            resolve().firstMethodOrNull {
                name = "updateIconsAndTextColors"
            }?.hook {
                before {
                    fldLightLockScreenWallpaper?.copy()?.of(this.instance)?.set(forceColorScheme == 2)
                }
            }
        }
    }
}