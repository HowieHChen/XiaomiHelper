package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object NotifWhitelist : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_NO_WHITELIST, extraCondition = { !Device.isInternationalBuild }) {
            "com.android.systemui.statusbar.notification.NotificationSettingsManager".toClassOrNull()
                ?.field {
                    name = "USE_WHITE_LISTS"
                    modifiers { isStatic }
                }?.get()?.setFalse()
        }
    }
}