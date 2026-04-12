package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.aiengine.CopyWebsite

object AIEngine : StaticHooker() {
    override fun onInit() {
        attach(CopyWebsite)
    }
}