package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object BluetoothRestrict : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_CONTROL_BLUETOOTH) {
            "com.android.settingslib.bluetooth.LocalBluetoothAdapter".toClass()
                .method {
                    name = "isSupportBluetoothRestrict"
                    param(ContextClass)
                }
                .hook {
                    replaceToFalse()
                }
        }
    }
}