package dev.lackluster.mihelper.hook.rules.taplus

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object Landscape : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.TAPLUS_LANDSCAPE) {
            "com.miui.contentextension.services.TextContentExtensionService".toClass()
                .method {
                    name = "isScreenPortrait"
                }
                .hook{
                    replaceToTrue()
                }
        }
    }
}