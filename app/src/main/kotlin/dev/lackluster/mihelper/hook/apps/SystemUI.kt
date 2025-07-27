package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.RemoveFreeformRestriction
import dev.lackluster.mihelper.hook.rules.systemui.DisableSmartDark
import dev.lackluster.mihelper.hook.rules.systemui.FuckStatusBarGestures
import dev.lackluster.mihelper.hook.rules.systemui.MonetOverlay
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.StatusBarActions
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCarrierLabel
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.HideDisturbNotification
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideStatusBarIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconPosition
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifFreeform
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifWhitelist
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.NotificationMaxNumber
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.StatusBarClock
import dev.lackluster.mihelper.hook.rules.systemui.freeform.HideTopBar
import dev.lackluster.mihelper.hook.rules.systemui.freeform.UnlockMultipleTask
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.CarrierTextView
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomElement
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomLayout
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomBackground
import dev.lackluster.mihelper.hook.rules.systemui.media.UnlockCustomAction
import dev.lackluster.mihelper.hook.rules.systemui.notif.MiuiXExpandButton
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.BatteryIndicator
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.ControlCenterBattery
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.ElementsFontWeight
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCellularIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideWiFiIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IgnoreSysHideIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.PadClockAnim
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.StatusBarDoubleTapToSleep

object SystemUI : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(ResourcesUtils)
        loadHooker(CarrierTextView)
        loadHooker(DoubleTapToSleep)
        loadHooker(HideDisturbNotification)

        loadHooker(NotifFreeform)
        loadHooker(NotifWhitelist)
        loadHooker(MiuiXExpandButton)
        loadHooker(MonetOverlay)

        loadHooker(IgnoreSysHideIcon)
        loadHooker(BatteryIndicator)
        loadHooker(ControlCenterBattery)
        loadHooker(HideCarrierLabel)
        loadHooker(HideCellularIcon)
        loadHooker(HideStatusBarIcon)
        loadHooker(HideWiFiIcon)
        loadHooker(IconPosition)
        loadHooker(NotificationMaxNumber)
        loadHooker(ElementsFontWeight)
        loadHooker(StatusBarClock)
        loadHooker(StatusBarDoubleTapToSleep)

        loadHooker(DisableSmartDark)
        loadHooker(StatusBarActions)
        loadHooker(FuckStatusBarGestures)

        loadHooker(CustomBackground)
        loadHooker(CustomLayout)
        loadHooker(CustomElement)
        return
        loadHooker(RemoveFreeformRestriction)
        loadHooker(UnlockMultipleTask)
        loadHooker(HideTopBar)


        loadHooker(UnlockCustomAction)
        loadHooker(PadClockAnim)
    }
}