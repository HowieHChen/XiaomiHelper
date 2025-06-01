package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.packageinstaller.AdBlocker
import dev.lackluster.mihelper.hook.rules.packageinstaller.BlockUploadAppInfo
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableCountCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableRiskCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.RemoveElement
import dev.lackluster.mihelper.hook.rules.packageinstaller.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.packageinstaller.CustomInstallSource
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisguiseNoNet
import dev.lackluster.mihelper.utils.DexKit

object PackageInstaller : YukiBaseHooker(){
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(ResourcesUtils)
        loadHooker(AdBlocker)
        loadHooker(RemoveElement)
        loadHooker(DisableCountCheck)
        loadHooker(DisableRiskCheck)
        loadHooker(BlockUploadAppInfo)
        loadHooker(CustomInstallSource)
        loadHooker(DisguiseNoNet)
        DexKit.closeDexKit()
    }
}