package dev.lackluster.mihelper.data

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable data object ModuleSettings : Route
    @Serializable data object SystemUI : Route
    @Serializable data object SystemFramework : Route
    @Serializable data object MiuiHome : Route
    @Serializable data object CleanMaster : Route
    @Serializable data object SecurityCenter : Route
    @Serializable data object Others : Route
    @Serializable data object About : Route

    @Serializable data object Menu : Route
    @Serializable data object StatusBarClock : Route
    @Serializable data object StatusBarFont : Route
    @Serializable data object IconTuner : Route
    @Serializable data object IconDetail : Route
    @Serializable data object MediaControl : Route
    @Serializable data object IslandMediaControl : Route
    @Serializable data object StackedMobileTuner : Route

    @Serializable data object DevUITest : Route
    @Serializable data object DevUITest2 : Route

    @Serializable data object DialogSearchCustomEngine : Route
    @Serializable data object DialogStatusBarIconPosition : Route
}
