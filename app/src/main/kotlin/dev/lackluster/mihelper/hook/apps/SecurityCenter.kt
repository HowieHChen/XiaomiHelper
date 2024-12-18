package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.securitycenter.AppDetailClickOpen
import dev.lackluster.mihelper.hook.rules.securitycenter.DisableRiskAppNotification
import dev.lackluster.mihelper.hook.rules.securitycenter.HideRedDot
import dev.lackluster.mihelper.hook.rules.securitycenter.LockScore
import dev.lackluster.mihelper.hook.rules.securitycenter.RemoveBubbleSettingsRestriction
import dev.lackluster.mihelper.hook.rules.securitycenter.RemoveReport
import dev.lackluster.mihelper.hook.rules.securitycenter.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.securitycenter.ShowScreenBatteryUsage
import dev.lackluster.mihelper.hook.rules.securitycenter.ChainStart
import dev.lackluster.mihelper.hook.rules.securitycenter.HideHomeElement
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipSplash
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipWarning
import dev.lackluster.mihelper.hook.rules.securitycenter.SystemAppWifiSettings
import dev.lackluster.mihelper.utils.DexKit

object SecurityCenter : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(ResourcesUtils)
        loadHooker(SkipWarning)
        loadHooker(ChainStart)
        loadHooker(LockScore)
        loadHooker(RemoveBubbleSettingsRestriction)
        loadHooker(DisableRiskAppNotification)
        loadHooker(ShowScreenBatteryUsage)
        loadHooker(RemoveReport)
        loadHooker(SystemAppWifiSettings)
        loadHooker(AppDetailClickOpen)
        loadHooker(SkipSplash)
        loadHooker(HideRedDot)
        loadHooker(HideHomeElement)
        DexKit.closeDexKit()
    }
}