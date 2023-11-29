package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.personalassist.BackgroundBlur

object PersonalAssist : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(BackgroundBlur)
    }
}