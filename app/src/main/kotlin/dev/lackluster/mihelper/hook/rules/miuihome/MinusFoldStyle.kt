package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object MinusFoldStyle : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_MINUS_FOLD_STYLE, extraCondition = {
            !Prefs.getBoolean(PrefKey.HOME_BLUR_REFACTOR, false)
        }) {
            "com.miui.home.launcher.overlay.assistant.AssistantDeviceAdapter".toClass()
                .method {
                    name = "inOverlapMode"
                }
                .hook {
                    replaceToTrue()
                }
        }
    }
}