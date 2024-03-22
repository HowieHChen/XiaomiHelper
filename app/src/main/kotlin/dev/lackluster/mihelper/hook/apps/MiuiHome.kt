package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.miuihome.AllowMoreFreeformForHome
import dev.lackluster.mihelper.hook.rules.miuihome.AlwaysShowTime
import dev.lackluster.mihelper.hook.rules.miuihome.AnimUnlock
import dev.lackluster.mihelper.hook.rules.miuihome.BlurEnableAll
import dev.lackluster.mihelper.hook.rules.miuihome.DisableFakeNavBar
import dev.lackluster.mihelper.hook.rules.miuihome.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.miuihome.FakePremium
import dev.lackluster.mihelper.hook.rules.miuihome.FolderAdaptIconSize
import dev.lackluster.mihelper.hook.rules.miuihome.BlurAdvancedEffect
import dev.lackluster.mihelper.hook.rules.miuihome.BlurEnhance
import dev.lackluster.mihelper.hook.rules.miuihome.BlurRadius
import dev.lackluster.mihelper.hook.rules.miuihome.FolderColumns
import dev.lackluster.mihelper.hook.rules.miuihome.IconCornerForLarge
import dev.lackluster.mihelper.hook.rules.miuihome.PerfectIcon
import dev.lackluster.mihelper.hook.rules.miuihome.IconUnblockGoogle
import dev.lackluster.mihelper.hook.rules.miuihome.MinusFoldStyle
import dev.lackluster.mihelper.hook.rules.miuihome.MinusSettings
import dev.lackluster.mihelper.hook.rules.miuihome.PadAllFeature
import dev.lackluster.mihelper.hook.rules.miuihome.PadDockCustom
import dev.lackluster.mihelper.hook.rules.miuihome.PadShowMemory
import dev.lackluster.mihelper.hook.rules.miuihome.RecentCardAnim
import dev.lackluster.mihelper.hook.rules.miuihome.RemoveReport
import dev.lackluster.mihelper.hook.rules.miuihome.ShowRealMemory
import dev.lackluster.mihelper.hook.rules.miuihome.StopWallpaperDarken
import dev.lackluster.mihelper.hook.rules.miuihome.WallpaperZoomSync
import dev.lackluster.mihelper.hook.rules.miuihome.WidgetAnim
import dev.lackluster.mihelper.hook.rules.miuihome.WidgetResizable

object MiuiHome : YukiBaseHooker() {
    override fun onHook() {
//        DexKit.initDexKit(this)
        loadHooker(AlwaysShowTime)
        loadHooker(AnimUnlock)
        loadHooker(BlurEnableAll)
        loadHooker(DisableFakeNavBar)
        loadHooker(DoubleTapToSleep)
        loadHooker(FakePremium)
        loadHooker(FolderAdaptIconSize)
        loadHooker(BlurAdvancedEffect)
        loadHooker(FolderColumns)
        loadHooker(IconUnblockGoogle)
        loadHooker(IconCornerForLarge)
        loadHooker(MinusFoldStyle)
        loadHooker(MinusSettings)
        loadHooker(PadAllFeature)
        loadHooker(PadShowMemory)
        loadHooker(PerfectIcon)
        loadHooker(RecentCardAnim)
        loadHooker(RemoveReport)
        loadHooker(ShowRealMemory)
        loadHooker(StopWallpaperDarken)
        loadHooker(WidgetAnim)
        loadHooker(WidgetResizable)
        loadHooker(PadDockCustom)
        loadHooker(WallpaperZoomSync)
        loadHooker(BlurRadius)
        loadHooker(BlurEnhance)
        loadHooker(AllowMoreFreeformForHome)
//        DexKit.closeDexKit()
    }
}