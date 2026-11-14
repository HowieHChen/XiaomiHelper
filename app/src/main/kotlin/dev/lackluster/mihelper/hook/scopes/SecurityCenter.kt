package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.securitycenter.AppDetailClickOpen
import dev.lackluster.mihelper.hook.rules.securitycenter.ChainStart
import dev.lackluster.mihelper.hook.rules.securitycenter.ClipboardToast
import dev.lackluster.mihelper.hook.rules.securitycenter.DisableRiskAppNotification
import dev.lackluster.mihelper.hook.rules.securitycenter.HideHomeElement
import dev.lackluster.mihelper.hook.rules.securitycenter.HideRedDot
import dev.lackluster.mihelper.hook.rules.securitycenter.LockScore
import dev.lackluster.mihelper.hook.rules.securitycenter.RemoveBubbleSettingsRestriction
import dev.lackluster.mihelper.hook.rules.securitycenter.RemoveReport
import dev.lackluster.mihelper.hook.rules.securitycenter.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.securitycenter.ShowScreenBatteryUsage
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipSplash
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipWarning
import dev.lackluster.mihelper.hook.rules.securitycenter.SystemAppWifiSettings

object SecurityCenter : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(ResourcesUtils)
        attach(ClipboardToast)
        attach(SkipWarning)
        attach(ChainStart)
        attach(RemoveBubbleSettingsRestriction)
        attach(DisableRiskAppNotification)
        attach(ShowScreenBatteryUsage)
        attach(SystemAppWifiSettings)
        attach(AppDetailClickOpen)
        attach(RemoveReport)
        attach(LockScore)
        attach(SkipSplash)
        attach(HideRedDot)
        attach(HideHomeElement)
    }
}