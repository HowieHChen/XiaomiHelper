package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.RemoveFreeformRestriction
import dev.lackluster.mihelper.hook.rules.systemui.BlockEditor
import dev.lackluster.mihelper.hook.rules.systemui.CustomMusicControl
import dev.lackluster.mihelper.hook.rules.systemui.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.systemui.HideBatteryIcon
import dev.lackluster.mihelper.hook.rules.systemui.HideCarrierLabel
import dev.lackluster.mihelper.hook.rules.systemui.HideDisturbNotification
import dev.lackluster.mihelper.hook.rules.systemui.HideStatusBarSpecialIcon
import dev.lackluster.mihelper.hook.rules.systemui.HideStatusBarIcon
import dev.lackluster.mihelper.hook.rules.systemui.HideUnlockTip
import dev.lackluster.mihelper.hook.rules.systemui.HideWifiActivityAndType
import dev.lackluster.mihelper.hook.rules.systemui.IconPosition
import dev.lackluster.mihelper.hook.rules.systemui.NotifFreeform
import dev.lackluster.mihelper.hook.rules.systemui.NotifSettingsRedirect
import dev.lackluster.mihelper.hook.rules.systemui.NotifWhitelist
import dev.lackluster.mihelper.hook.rules.systemui.NotificationMaxNumber
import dev.lackluster.mihelper.hook.rules.systemui.StatusBarClock
import dev.lackluster.mihelper.hook.rules.systemui.freeform.HideTopBar
import dev.lackluster.mihelper.hook.rules.systemui.freeform.UnlockMultipleTask

object SystemUI : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(RemoveFreeformRestriction)
        loadHooker(UnlockMultipleTask)
        loadHooker(HideTopBar)
        loadHooker(HideStatusBarIcon)
        loadHooker(HideBatteryIcon)
        loadHooker(HideCarrierLabel)
        loadHooker(HideStatusBarSpecialIcon)
        loadHooker(HideWifiActivityAndType)
        loadHooker(StatusBarClock)
        loadHooker(BlockEditor)
        loadHooker(DoubleTapToSleep)
        loadHooker(HideDisturbNotification)
        loadHooker(HideUnlockTip)
        loadHooker(IconPosition)
        loadHooker(NotificationMaxNumber)
        loadHooker(NotifFreeform)
        loadHooker(NotifSettingsRedirect)
        loadHooker(NotifWhitelist)
        loadHooker(CustomMusicControl)
    }
}