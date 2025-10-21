package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.android.AllowMoreFreeform
import dev.lackluster.mihelper.hook.rules.android.DarkModeForAll
import dev.lackluster.mihelper.hook.rules.android.DisableFixedOrientation
import dev.lackluster.mihelper.hook.rules.android.FontScale
import dev.lackluster.mihelper.hook.rules.android.RemoveFreeformRestriction
import dev.lackluster.mihelper.hook.rules.android.WallpaperScaleRatio

object Android : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(DarkModeForAll)
        loadHooker(RemoveFreeformRestriction)
        loadHooker(AllowMoreFreeform)
        loadHooker(FontScale)
        return
        loadHooker(DisableFixedOrientation)
        loadHooker(WallpaperScaleRatio)
    }
}