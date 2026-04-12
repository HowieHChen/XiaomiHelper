package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.updater.BlockAutoUpdateDialog
import dev.lackluster.mihelper.hook.rules.updater.DisableValidation

object Updater : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(DisableValidation)
        attach(BlockAutoUpdateDialog)
    }
}