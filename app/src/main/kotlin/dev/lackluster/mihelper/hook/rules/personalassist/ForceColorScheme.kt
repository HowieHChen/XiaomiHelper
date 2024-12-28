package dev.lackluster.mihelper.hook.rules.personalassist

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object ForceColorScheme : YukiBaseHooker() {
    private val forceColorSchemeMinus = Prefs.getInt(Pref.Key.MiuiHome.FORCE_COLOR_MINUS, 0).convertToColorMode()
    private val wallpaperUtilsClass by lazy {
        "com.miui.personalassistant.utils.wallpaper.WallpaperUtils".toClass()
    }

    override fun onHook() {
        if (forceColorSchemeMinus > -1) {
            wallpaperUtilsClass.apply {
                method {
                    name = "setCurrentWallpaperColorMode"
                }.hook {
                    before {
                        this.args(0).set(forceColorSchemeMinus)
                    }
                }
                method {
                    name = "setCurrentStatusBarAreaColorMode"
                }.hook {
                    before {
                        this.args(0).set(forceColorSchemeMinus)
                    }
                }
                method {
                    name = "setCurrentWallPaperHeadColorMode"
                }.hook {
                    before {
                        this.args(0).set(forceColorSchemeMinus)
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