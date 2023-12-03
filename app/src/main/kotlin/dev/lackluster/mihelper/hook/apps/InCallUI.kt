package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.incallui.HideCRBT

object InCallUI : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HideCRBT)
    }
}