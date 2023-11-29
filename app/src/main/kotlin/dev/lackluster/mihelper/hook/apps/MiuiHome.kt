package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.miuihome.AlwaysShowTime
import dev.lackluster.mihelper.hook.rules.miuihome.AnimEnhance
import dev.lackluster.mihelper.hook.rules.miuihome.AnimUnlock
import dev.lackluster.mihelper.hook.rules.miuihome.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.miuihome.IconCornerForLarge
import dev.lackluster.mihelper.hook.rules.miuihome.PerfectIcon
import dev.lackluster.mihelper.hook.rules.miuihome.IconUnblockGoogle
import dev.lackluster.mihelper.hook.rules.miuihome.MinusFoldStyle
import dev.lackluster.mihelper.hook.rules.miuihome.MinusSettings
import dev.lackluster.mihelper.hook.rules.miuihome.PadShowMemory
import dev.lackluster.mihelper.hook.rules.miuihome.RecentCardAnim
import dev.lackluster.mihelper.hook.rules.miuihome.ShowRealMemory
import dev.lackluster.mihelper.hook.rules.miuihome.StopWallpaperDarken
import dev.lackluster.mihelper.hook.rules.miuihome.WidgetAnim
import dev.lackluster.mihelper.hook.rules.miuihome.WidgetResizable
import dev.lackluster.mihelper.utils.DexKit

object MiuiHome : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AlwaysShowTime)
        loadHooker(AnimEnhance)
        loadHooker(AnimUnlock)
        loadHooker(DoubleTapToSleep)
        loadHooker(IconUnblockGoogle)
        loadHooker(IconCornerForLarge)
        loadHooker(MinusFoldStyle)
        loadHooker(MinusSettings)
        loadHooker(PadShowMemory)
        loadHooker(PerfectIcon)
        loadHooker(RecentCardAnim)
        loadHooker(ShowRealMemory)
        loadHooker(StopWallpaperDarken)
        loadHooker(WidgetAnim)
        loadHooker(WidgetResizable)
        DexKit.closeDexKit()
    }
}