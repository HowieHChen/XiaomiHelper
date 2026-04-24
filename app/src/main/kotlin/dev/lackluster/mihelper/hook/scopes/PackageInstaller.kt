package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.packageinstaller.AdBlocker
import dev.lackluster.mihelper.hook.rules.packageinstaller.BlockUploadAppInfo
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableCountCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableRiskCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.RemoveElement
import dev.lackluster.mihelper.hook.rules.packageinstaller.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.packageinstaller.CustomInstallSource
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisguiseNoNet

object PackageInstaller : StaticHooker(){
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(ResourcesUtils)
        attach(AdBlocker)
        attach(RemoveElement)
        attach(DisableCountCheck)
        attach(DisableRiskCheck)
        attach(BlockUploadAppInfo)
        attach(CustomInstallSource)
        attach(DisguiseNoNet)
    }
}