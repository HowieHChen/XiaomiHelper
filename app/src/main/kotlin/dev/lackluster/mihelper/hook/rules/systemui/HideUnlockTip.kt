package dev.lackluster.mihelper.hook.rules.systemui

import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs

object HideUnlockTip : YukiBaseHooker() {
    override fun onHook() {
        Prefs.hasEnable(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_UNLOCK_TIP) {
            "com.android.systemui.keyguard.KeyguardIndicationRotateTextViewController".toClass()
                .method {
                    name = "hasIndicationsExceptResting"
                }
                .hook {
                    replaceToTrue()
                }
            "com.android.systemui.statusbar.KeyguardIndicationController".toClass()
                .method {
                    name = "setIndicationArea"
                }
                .hook {
                    after {
                        (this.instance.current().field {
                            name = "mUpArrow"
                        }.any() as? ImageView)?.alpha = 0f
                    }
                }
        }
    }
}