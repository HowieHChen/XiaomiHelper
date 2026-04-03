package dev.lackluster.mihelper.data

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey, Parcelable {
    @Serializable @Parcelize data object ModuleSettings : Route
    @Serializable @Parcelize data object SystemUI : Route
    @Serializable @Parcelize data object SystemFramework : Route
    @Serializable @Parcelize data object MiuiHome : Route
    @Serializable @Parcelize data object CleanMaster : Route
    @Serializable @Parcelize data object SecurityCenter : Route
    @Serializable @Parcelize data object Others : Route
    @Serializable @Parcelize data object About : Route

    @Serializable @Parcelize data object Menu : Route
    @Serializable @Parcelize data object StatusBarClock : Route
    @Serializable @Parcelize data object StatusBarFont : Route
    @Serializable @Parcelize data object IconTuner : Route
    @Serializable @Parcelize data object IconDetail : Route
    @Serializable @Parcelize data object NotifMediaControl : Route
    @Serializable @Parcelize data object IslandMediaControl : Route
    @Serializable @Parcelize data object DevUITest : Route
    @Serializable @Parcelize data object DevUITest2 : Route

    @Serializable @Parcelize data object StatusBarIconPosition : Route
}
