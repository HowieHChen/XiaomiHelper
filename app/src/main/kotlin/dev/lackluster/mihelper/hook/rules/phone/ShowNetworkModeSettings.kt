package dev.lackluster.mihelper.hook.rules.phone

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object ShowNetworkModeSettings : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.PHONE_NETWORK_MODE_SETTINGS) {
            "com.android.phone.NetworkModeManager".toClass()
                .method {
                    name = "isRemoveNetworkModeSettings"
                }
                .hook {
                    replaceToFalse()
                }
        }
    }
}