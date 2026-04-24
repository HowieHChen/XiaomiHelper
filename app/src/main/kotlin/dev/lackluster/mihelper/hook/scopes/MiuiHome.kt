package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.miuihome.AllowMoreFreeformForHome
import dev.lackluster.mihelper.hook.rules.miuihome.recent.HideFakeNavBar
import dev.lackluster.mihelper.hook.rules.miuihome.ForceColorScheme
import dev.lackluster.mihelper.hook.rules.miuihome.RemoveHotSeatNumLimit
import dev.lackluster.mihelper.hook.rules.miuihome.folder.FolderAdaptIconSize
import dev.lackluster.mihelper.hook.rules.miuihome.minus.MinusSettings
import dev.lackluster.mihelper.hook.rules.miuihome.recent.RecentCardAnim
import dev.lackluster.mihelper.hook.rules.miuihome.RemoveReport
import dev.lackluster.mihelper.hook.rules.miuihome.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.miuihome.anim.DisableIconAnim
import dev.lackluster.mihelper.hook.rules.miuihome.recent.ShowRealMemory
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.BackGestureHaptic
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.LineGesture
import dev.lackluster.mihelper.hook.rules.miuihome.gesture.PredictiveBackProgress
import dev.lackluster.mihelper.hook.rules.miuihome.recent.HideClearButton

object MiuiHome : StaticHooker() {
    override fun onInit() {
        attach(ResourcesUtils)

        attach(DisableIconAnim)
        attach(FolderAdaptIconSize)
        attach(BackGestureHaptic)
        attach(LineGesture)
        attach(PredictiveBackProgress)

        attach(MinusSettings)

        attach(HideClearButton)
        attach(HideFakeNavBar)
        attach(RecentCardAnim)
        attach(ShowRealMemory)

        attach(AllowMoreFreeformForHome)
        attach(ForceColorScheme)
        attach(RemoveHotSeatNumLimit)
        attach(RemoveReport)
    }
}