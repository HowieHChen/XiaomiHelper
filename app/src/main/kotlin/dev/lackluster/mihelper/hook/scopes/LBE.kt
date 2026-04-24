package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.lbe.BlockRemoveAutoStart
import dev.lackluster.mihelper.hook.rules.lbe.ClipboardToast

object LBE : StaticHooker() {
    override fun onInit() {
        attach(BlockRemoveAutoStart)
        attach(ClipboardToast)
    }
}