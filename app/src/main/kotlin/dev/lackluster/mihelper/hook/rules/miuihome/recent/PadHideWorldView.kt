package dev.lackluster.mihelper.hook.rules.miuihome.recent

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object PadHideWorldView : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.PAD_RECENT_HIDE_WORLD) {
            "com.miui.home.recents.views.RecentsDecorations".toClass().apply {
                method {
                    name = "setAlphaAndVisibilityForWorldContainer"
                }.hook {
                    before {
                        this.args(1).set(View.GONE)
                    }
                }
            }
        }
    }
}