package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.guardprovider.BlockEnvCheck
import dev.lackluster.mihelper.hook.rules.guardprovider.BlockUploadAppList

object GuardProvider: StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(BlockUploadAppList)
        attach(BlockEnvCheck)
    }
}