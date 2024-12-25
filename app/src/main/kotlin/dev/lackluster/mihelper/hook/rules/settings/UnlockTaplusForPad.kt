package dev.lackluster.mihelper.hook.rules.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object UnlockTaplusForPad : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Settings.UNLOCK_TAPLUS_FOR_PAD, extraCondition = { Device.isPad }) {
            "com.android.settings.utils.SettingsFeatures".toClass().method {
                name = "isNeedRemoveContentExtension"
            }.hook {
                replaceToFalse()
            }
        }
    }
}