package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object StopWallpaperDarken : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_WALLPAPER_DARKEN) {
            "com.miui.home.recents.DimLayer".toClass()
                .method {
                    name = "dim"
                    paramCount = 3
                }
                .hook {
                    before {
                        this.args(0).set(0.0f)
                        this.instance.current().field {
                            name = "mCurrentAlpha"
                        }.set(0.0f)
                    }
                }
        }
    }
}