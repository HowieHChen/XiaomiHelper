package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ForceColorScheme : YukiBaseHooker() {
    private val forceColorSchemeStatusBar = Prefs.getInt(Pref.Key.MiuiHome.FORCE_COLOR_STATUS_BAR, 0).convertToColorMode()
    private val forceColorSchemeTextIcon = Prefs.getInt(Pref.Key.MiuiHome.FORCE_COLOR_TEXT_ICON, 0).convertToColorMode()
    private val wallpaperUtilsClass by lazy {
        "com.miui.home.launcher.WallpaperUtils".toClass()
    }

    override fun onHook() {
        if (forceColorSchemeStatusBar + forceColorSchemeTextIcon > -2) {
            if (forceColorSchemeTextIcon > -1) {
                wallpaperUtilsClass.method {
                    name = "setCurrentWallpaperColorMode"
                }.hook {
                    before {
                        this.args(0).set(forceColorSchemeTextIcon)
                    }
                }
                wallpaperUtilsClass.method {
                    name = "setCurrentSearchBarAreaColorMode"
                }.hook {
                    before {
                        this.args(0).set(forceColorSchemeTextIcon)
                    }
                }
            }
            if (forceColorSchemeStatusBar > -1) {
                wallpaperUtilsClass.method {
                    name = "setCurrentStatusBarAreaColorMode"
                }.hook {
                    before {
                        this.args(0).set(forceColorSchemeStatusBar)
                    }
                }
            }
        }
    }

    private fun Int.convertToColorMode(): Int {
        return when(this) {
            1 -> 0
            2 -> 2
            else -> -1
        }
    }
}