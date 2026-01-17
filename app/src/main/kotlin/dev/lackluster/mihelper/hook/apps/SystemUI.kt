package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils
import dev.lackluster.mihelper.hook.rules.systemui.MonetOverlay
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.StatusBarActions
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.CarrierLabelFontWeight
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.HideDisturbNotification
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.HideNextAlarm
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.KeepNotification
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.LockscreenDoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.systemui.lockscreen.StatusBarClockContainer
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomBackground
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomElement
import dev.lackluster.mihelper.hook.rules.systemui.media.CustomLayout
import dev.lackluster.mihelper.hook.rules.systemui.notif.ExpandNotification
import dev.lackluster.mihelper.hook.rules.systemui.notif.LayoutAndRankOpt
import dev.lackluster.mihelper.hook.rules.systemui.notif.MiuiXExpandButton
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifFreeform
import dev.lackluster.mihelper.hook.rules.systemui.notif.NotifWhitelist
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.BatteryIndicator
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.CellularIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.BatteryIndicatorStyle
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.CellularTypeIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCarrierLabel
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.HideCellularIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconManager
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconPosition
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IgnoreSysIconSettings
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.MiuiClock
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.MiuiClockFontWeight
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.NetworkSpeed
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.WifiIcon
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.NotificationMaxNumber
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.RegionSampling
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.StatusBarDoubleTapToSleep

object SystemUI : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(ResourcesUtils)
        loadHooker(CommonClassUtils)
        loadHooker(MonetOverlay)
        loadHooker(StatusBarActions)

        loadHooker(LockscreenDoubleTapToSleep)
        loadHooker(HideDisturbNotification)
        loadHooker(HideNextAlarm)
        loadHooker(KeepNotification)
        loadHooker(StatusBarClockContainer)
        loadHooker(CarrierLabelFontWeight)

        loadHooker(CustomBackground)
        loadHooker(CustomLayout)
        loadHooker(CustomElement)

        loadHooker(MiuiXExpandButton)
        loadHooker(NotifFreeform)
        loadHooker(NotifWhitelist)
        loadHooker(ExpandNotification)
        loadHooker(LayoutAndRankOpt)


        loadHooker(BatteryIndicator)
        loadHooker(BatteryIndicatorStyle)
        loadHooker(CellularIcon)
        loadHooker(CellularTypeIcon)
        loadHooker(HideCarrierLabel)
        loadHooker(HideCellularIcon)
        loadHooker(IconManager)
        loadHooker(IconPosition)
        loadHooker(IgnoreSysIconSettings)
        loadHooker(MiuiClock)
        loadHooker(MiuiClockFontWeight)
        loadHooker(NetworkSpeed)
        loadHooker(NotificationMaxNumber)

        loadHooker(RegionSampling)
        loadHooker(StatusBarDoubleTapToSleep)
        loadHooker(WifiIcon)
//        loadHooker(UnlockMultipleTask)
//        loadHooker(HideTopBar)


//        loadHooker(UnlockCustomAction)
//        loadHooker(PadClockAnim)
    }
}