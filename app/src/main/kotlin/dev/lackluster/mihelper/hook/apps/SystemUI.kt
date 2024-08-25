package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.RemoveFreeformRestriction
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.BlockEditor
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCarrierLabel
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.HideDisturbNotification
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideStatusBarSpecialIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideStatusBarIcon
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.HideUnlockTip
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideWifiActivityAndType
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconPosition
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifFreeform
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifSettingsRedirect
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifWhitelist
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.NotificationMaxNumber
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.StatusBarClock
import dev.lackluster.mihelper.hook.rules.systemui.freeform.HideTopBar
import dev.lackluster.mihelper.hook.rules.systemui.freeform.UnlockMultipleTask
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.CarrierText
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomElement
import dev.lackluster.mihelper.hook.rules.systemui.media.StyleCustomHookEntry
import dev.lackluster.mihelper.hook.rules.systemui.media.UnlockCustomAction
import dev.lackluster.mihelper.hook.rules.systemui.notif.AdvancedTextures
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.BatteryIndicator
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.PadClockAnim

object SystemUI : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(ResourcesUtils)
        loadHooker(RemoveFreeformRestriction)
        loadHooker(UnlockMultipleTask)
        loadHooker(HideTopBar)
        loadHooker(HideStatusBarIcon)
        loadHooker(BatteryIndicator)
        loadHooker(HideCarrierLabel)
        loadHooker(HideStatusBarSpecialIcon)
        loadHooker(HideWifiActivityAndType)
        loadHooker(StatusBarClock)
        loadHooker(BlockEditor)
        loadHooker(dev.lackluster.mihelper.hook.rules.systemui.statusbar.DoubleTapToSleep)
        loadHooker(DoubleTapToSleep)
        loadHooker(HideDisturbNotification)
        loadHooker(HideUnlockTip)
        loadHooker(IconPosition)
        loadHooker(NotificationMaxNumber)
        loadHooker(NotifFreeform)
        loadHooker(NotifSettingsRedirect)
        loadHooker(NotifWhitelist)
        loadHooker(AdvancedTextures)
        loadHooker(StyleCustomHookEntry)
        loadHooker(UnlockCustomAction)
        loadHooker(CustomElement)
        loadHooker(CarrierText)
        loadHooker(PadClockAnim)
    }
}