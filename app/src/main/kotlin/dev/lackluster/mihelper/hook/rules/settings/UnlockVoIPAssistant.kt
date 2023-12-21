package dev.lackluster.mihelper.hook.rules.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object UnlockVoIPAssistant : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SETTINGS_UNLOCK_VOIP_ASSISTANT) {
            "com.android.settings.lab.MiuiVoipAssistantController".toClass()
                .method {
                    name = "isNotSupported"
                }
                .hook {
                    replaceToFalse()
                }
        }
    }
}