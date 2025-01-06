package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.android.AllowMoreFreeform
import dev.lackluster.mihelper.hook.rules.android.DarkModeForAll
import dev.lackluster.mihelper.hook.rules.android.DisableFixedOrientation
import dev.lackluster.mihelper.hook.rules.shared.RemoveFreeformRestriction
import dev.lackluster.mihelper.hook.rules.android.WallpaperScaleRatio

object Android : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(DarkModeForAll)
        return
        loadHooker(DisableFixedOrientation)
        loadHooker(RemoveFreeformRestriction)
        loadHooker(AllowMoreFreeform)
        loadHooker(WallpaperScaleRatio)
    }
}