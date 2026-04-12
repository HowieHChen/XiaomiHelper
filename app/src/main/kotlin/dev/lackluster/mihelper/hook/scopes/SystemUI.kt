package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils
import dev.lackluster.mihelper.hook.rules.systemui.MonetOverlay
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.StatusBarActions
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.CarrierLabelFontWeight
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.ForceColorScheme
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.HideDisturbNotification
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.KeepNotification
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.LockscreenDoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.StatusBarClockContainer
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomBackground
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomElement
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomLayout
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomProgressBar
import dev.lackluster.mihelper.hook.rules.systemui.media.UnlockCustomAction
import dev.lackluster.mihelper.hook.rules.systemui.notif.ExpandNotification
import dev.lackluster.mihelper.hook.rules.systemui.notif.LayoutAndRankOpt
import dev.lackluster.mihelper.hook.rules.systemui.notif.MiuiXExpandButton
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifFreeform
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifWhitelist
import dev.lackluster.mihelper.hook.rules.systemui.plugin.PluginFactory
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.BatteryIndicator
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.CellularIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.BatteryIndicatorStyle
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.CellularTypeIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCarrierLabel
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCellularIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconManager
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IgnoreSysIconSettings
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.CompoundIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.MiuiClock
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.MiuiClockFontWeight
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.NetworkSpeed
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.WifiIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.NotificationMaxNumber
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.RegionSampling
import dev.lackluster.mihelper.hook.rules.systemui.mobile.StackedMobileIcon
import dev.lackluster.mihelper.hook.rules.systemui.notif.SuppressFold
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.StatusBarTapGesture

object SystemUI : StaticHooker() {
    override fun onInit() {
        attach(ResourcesUtils)
        attach(CommonClassUtils)
        attach(MonetOverlay)
        attach(StatusBarActions)
        attach(PluginFactory)

        attach(LockscreenDoubleTapToSleep)
        attach(HideDisturbNotification)
        attach(KeepNotification)
        attach(StatusBarClockContainer)
        attach(CarrierLabelFontWeight)
        attach(ForceColorScheme)

        attach(CustomBackground)
        attach(CustomLayout)
        attach(CustomElement)
        attach(CustomProgressBar)
        attach(UnlockCustomAction)

        attach(MiuiXExpandButton)
        attach(NotifFreeform)
        attach(NotifWhitelist)
        attach(ExpandNotification)
        attach(LayoutAndRankOpt)
        attach(SuppressFold)

        attach(BatteryIndicator)
        attach(BatteryIndicatorStyle)
        attach(CellularIcon)
        attach(CellularTypeIcon)
        attach(HideCarrierLabel)
        attach(HideCellularIcon)
        attach(IconManager)
        attach(IgnoreSysIconSettings)
        attach(CompoundIcon)
        attach(MiuiClock)
        attach(MiuiClockFontWeight)
        attach(NetworkSpeed)
        attach(NotificationMaxNumber)
        attach(StackedMobileIcon)
        attach(RegionSampling)
        attach(StatusBarTapGesture)
        attach(WifiIcon)
//        attach(UnlockMultipleTask)
//        attach(HideTopBar)
//        attach(PadClockAnim)
    }
}