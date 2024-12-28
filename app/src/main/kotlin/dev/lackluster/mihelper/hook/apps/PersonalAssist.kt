package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.personalassist.BackgroundBlur
import dev.lackluster.mihelper.hook.rules.personalassist.BlockOriginalBlur
import dev.lackluster.mihelper.hook.rules.personalassist.ForceColorScheme

object PersonalAssist : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(BackgroundBlur)
        loadHooker(BlockOriginalBlur)
        loadHooker(ForceColorScheme)
    }
}