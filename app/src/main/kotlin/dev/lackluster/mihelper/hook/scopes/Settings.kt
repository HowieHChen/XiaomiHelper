package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.settings.FontScale
import dev.lackluster.mihelper.hook.rules.settings.HeaderList
import dev.lackluster.mihelper.hook.rules.settings.QuickPermission

object Settings : StaticHooker() {
    override fun onInit() {
        attach(FontScale)
        attach(HeaderList)
        attach(QuickPermission)
    }
}