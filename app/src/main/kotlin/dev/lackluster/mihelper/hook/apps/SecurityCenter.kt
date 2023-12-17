package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.securitycenter.DisableRiskAppNotification
import dev.lackluster.mihelper.hook.rules.securitycenter.LockScore
import dev.lackluster.mihelper.hook.rules.securitycenter.RemoveBubbleSettingsRestriction
import dev.lackluster.mihelper.hook.rules.securitycenter.RemoveReport
import dev.lackluster.mihelper.hook.rules.securitycenter.ShowScreenBatteryUsage
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipOpenApp
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipWarning
import dev.lackluster.mihelper.hook.rules.securitycenter.SystemAppWifiSettings
import dev.lackluster.mihelper.utils.DexKit

object SecurityCenter : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(SkipWarning)
        loadHooker(SkipOpenApp)
        loadHooker(LockScore)
        loadHooker(RemoveBubbleSettingsRestriction)
        loadHooker(DisableRiskAppNotification)
        loadHooker(ShowScreenBatteryUsage)
        loadHooker(RemoveReport)
        loadHooker(SystemAppWifiSettings)
        DexKit.closeDexKit()
    }
}