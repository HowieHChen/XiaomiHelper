package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.personalassist.ForceColorScheme

object PersonalAssist : StaticHooker() {
    override fun onInit() {
        attach(ForceColorScheme)
    }
}