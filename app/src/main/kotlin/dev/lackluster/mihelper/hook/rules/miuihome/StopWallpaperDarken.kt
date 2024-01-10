package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs

object StopWallpaperDarken : YukiBaseHooker() {
    private val stopDarken =
        Prefs.getBoolean(PrefKey.HOME_WALLPAPER_DARKEN, false) || Prefs.getBoolean(PrefKey.HOME_BLUR_REFACTOR, false)
    override fun onHook() {
        if (stopDarken) {
            "com.miui.home.recents.DimLayer".toClass()
                .method {
                    name = "isSupportDim"
                }
                .hook {
                    replaceToFalse()
                }
        }
    }
}