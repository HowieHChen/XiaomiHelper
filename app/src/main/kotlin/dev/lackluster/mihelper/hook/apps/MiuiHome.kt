package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.miuihome.AllowMoreFreeformForHome
import dev.lackluster.mihelper.hook.rules.miuihome.AlwaysShowTime
import dev.lackluster.mihelper.hook.rules.miuihome.anim.AnimUnlock
import dev.lackluster.mihelper.hook.rules.miuihome.recent.DisableFakeNavBar
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.miuihome.FakePremium
import dev.lackluster.mihelper.hook.rules.miuihome.ForceColorScheme
import dev.lackluster.mihelper.hook.rules.miuihome.folder.FolderAdaptIconSize
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
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.BackGestureHaptic
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.LineGesture
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.QuickSwitch
import dev.lackluster.mihelper.hook.rules.miuihome.recent.HideClearButton
import dev.lackluster.mihelper.hook.rules.miuihome.recent.PadHideWorldView
import dev.lackluster.mihelper.hook.rules.miuihome.refactor.BlurRefactorEntry
import dev.lackluster.mihelper.hook.rules.miuihome.widget.WidgetAnim
import dev.lackluster.mihelper.hook.rules.miuihome.widget.WidgetResizable
import dev.lackluster.mihelper.utils.Device

object MiuiHome : YukiBaseHooker() {
    override fun onHook() {
//        DexKit.initDexKit(this)
        loadHooker(ResourcesUtils)
        loadHooker(DisableIconAnim)
        loadHooker(FolderAdaptIconSize)
        loadHooker(BackGestureHaptic)
        loadHooker(DoubleTapToSleep)
        loadHooker(QuickSwitch)
        loadHooker(LineGesture)
        loadHooker(HideClearButton)
        loadHooker(RecentCardAnim)
        loadHooker(ShowRealMemory)
        loadHooker(ForceColorScheme)
        loadHooker(RemoveReport)
        if (Device.isPad) {
            loadHooker(PadShowMemory)
            loadHooker(PadHideWorldView)
        }
        return
        loadHooker(AdvancedTexture)
        loadHooker(ShortcutMenu)
        loadHooker(AlwaysShowTime)
        loadHooker(AnimUnlock)
        loadHooker(DisableFakeNavBar)
        loadHooker(FakePremium)
        loadHooker(FolderColumns)
        loadHooker(IconUnblockGoogle)
        loadHooker(IconCornerForLarge)
        loadHooker(MinusFoldStyle)
        loadHooker(MinusSettings)
        loadHooker(PerfectIcon)
        loadHooker(StopWallpaperDarken)
        loadHooker(WidgetAnim)
        loadHooker(WidgetResizable)
        loadHooker(PadDockCustom)
        loadHooker(WallpaperZoomSync)
//        loadHooker(BlurRefactor)
        loadHooker(AllowMoreFreeformForHome)
//        DexKit.closeDexKit()
        loadHooker(BlurRefactorEntry)
    }
}