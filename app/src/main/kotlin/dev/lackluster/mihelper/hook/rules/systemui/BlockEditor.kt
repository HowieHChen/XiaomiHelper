package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object BlockEditor : YukiBaseHooker(){
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_LOCKSCREEN_BLOCK_EDITOR) {
            "com.android.keyguard.KeyguardEditorHelper".toClass()
                .method {
                    name = "checkIfStartEditActivity"
                }
                .hook {
                    intercept()
                }
        }
    }
}