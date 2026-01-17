package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
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

object MiuiHome : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(ResourcesUtils)

        loadHooker(DisableIconAnim)
        loadHooker(FolderAdaptIconSize)
        loadHooker(BackGestureHaptic)
        loadHooker(LineGesture)
        loadHooker(PredictiveBackProgress)

        loadHooker(MinusSettings)

        loadHooker(HideClearButton)
        loadHooker(HideFakeNavBar)
        loadHooker(RecentCardAnim)
        loadHooker(ShowRealMemory)

        loadHooker(AllowMoreFreeformForHome)
        loadHooker(ForceColorScheme)
        loadHooker(RemoveHotSeatNumLimit)
        loadHooker(RemoveReport)
    }
}