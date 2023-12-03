package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.systemui.BluetoothRestrict
import dev.lackluster.mihelper.hook.rules.systemui.DoubleTapToSleep
import dev.lackluster.mihelper.hook.rules.systemui.HideDisturbNotification
import dev.lackluster.mihelper.hook.rules.systemui.HideUnlockTip
import dev.lackluster.mihelper.hook.rules.systemui.LockScreenFont
import dev.lackluster.mihelper.hook.rules.systemui.MediaControlMonet
import dev.lackluster.mihelper.hook.rules.systemui.MediaControlOptimize
import dev.lackluster.mihelper.hook.rules.systemui.NotifFreeform
import dev.lackluster.mihelper.hook.rules.systemui.NotifSettingsRedirect
import dev.lackluster.mihelper.hook.rules.systemui.NotifWhitelist

object SystemUI : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(BluetoothRestrict)
        loadHooker(DoubleTapToSleep)
        loadHooker(HideDisturbNotification)
        loadHooker(HideUnlockTip)
        loadHooker(LockScreenFont)
        loadHooker(MediaControlMonet)
        loadHooker(MediaControlOptimize)
        loadHooker(NotifFreeform)
        loadHooker(NotifSettingsRedirect)
        loadHooker(NotifWhitelist)
    }
}