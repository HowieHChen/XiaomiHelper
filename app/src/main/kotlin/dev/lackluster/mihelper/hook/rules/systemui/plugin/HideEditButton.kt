package dev.lackluster.mihelper.hook.rules.systemui.plugin

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideEditButton : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.SystemUI.Plugin.CONTROL_CENTER_HIDE_EDIT) {
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "available"
                }?.hook {
                    replaceToFalse()
                }
            }
        }
    }
}