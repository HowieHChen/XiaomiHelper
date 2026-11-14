package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.updater.BlockAutoUpdateDialog
import dev.lackluster.mihelper.hook.rules.updater.DisableValidation
import dev.lackluster.mihelper.hook.rules.updater.SotaFilter

object Updater : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(DisableValidation)
        attach(BlockAutoUpdateDialog)
        attach(SotaFilter)
    }
}