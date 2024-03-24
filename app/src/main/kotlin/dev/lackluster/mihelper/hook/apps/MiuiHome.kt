package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.miuihome.AllowMoreFreeformForHome
import dev.lackluster.mihelper.hook.rules.miuihome.AlwaysShowTime
import dev.lackluster.mihelper.hook.rules.miuihome.anim.AnimUnlock
import dev.lackluster.mihelper.hook.rules.miuihome.recent.DisableFakeNavBar
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.miuihome.FakePremium
import dev.lackluster.mihelper.hook.rules.miuihome.folder.FolderAdaptIconSize
import dev.lackluster.mihelper.hook.rules.miuihome.BlurEnhance
import dev.lackluster.mihelper.hook.rules.miuihome.folder.FolderColumns
import dev.lackluster.mihelper.hook.rules.miuihome.icon.IconCornerForLarge
import dev.lackluster.mihelper.hook.rules.miuihome.icon.PerfectIcon
import dev.lackluster.mihelper.hook.rules.miuihome.icon.IconUnblockGoogle
import dev.lackluster.mihelper.hook.rules.miuihome.minus.MinusFoldStyle
import dev.lackluster.mihelper.hook.rules.miuihome.minus.MinusSettings
import dev.lackluster.mihelper.hook.rules.miuihome.recent.PadDockCustom
import dev.lackluster.mihelper.hook.rules.miuihome.recent.PadShowMemory
import dev.lackluster.mihelper.hook.rules.miuihome.recent.RecentCardAnim
import dev.lackluster.mihelper.hook.rules.miuihome.RemoveReport
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.miuihome.ShortcutMenu
import dev.lackluster.mihelper.hook.rules.miuihome.anim.DisableIconAnim
import dev.lackluster.mihelper.hook.rules.miuihome.recent.ShowRealMemory
import dev.lackluster.mihelper.hook.rules.miuihome.recent.StopWallpaperDarken
import dev.lackluster.mihelper.hook.rules.miuihome.anim.WallpaperZoomSync
import dev.lackluster.mihelper.hook.rules.miuihome.folder.AdvancedTexture
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.QuickBack
import dev.lackluster.mihelper.hook.rules.miuihome.widget.WidgetAnim
import dev.lackluster.mihelper.hook.rules.miuihome.widget.WidgetResizable

object MiuiHome : YukiBaseHooker() {
    override fun onHook() {
//        DexKit.initDexKit(this)
        loadHooker(ResourcesUtils)
        loadHooker(AdvancedTexture)
        loadHooker(DisableIconAnim)
        loadHooker(ShortcutMenu)
        loadHooker(QuickBack)
        loadHooker(AlwaysShowTime)
        loadHooker(AnimUnlock)
        loadHooker(DisableFakeNavBar)
        loadHooker(DoubleTapToSleep)
        loadHooker(FakePremium)
        loadHooker(FolderAdaptIconSize)
        loadHooker(FolderColumns)
        loadHooker(IconUnblockGoogle)
        loadHooker(IconCornerForLarge)
        loadHooker(MinusFoldStyle)
        loadHooker(MinusSettings)
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
        loadHooker(BlurEnhance)
        loadHooker(AllowMoreFreeformForHome)
//        DexKit.closeDexKit()
    }
}