package dev.lackluster.mihelper.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import dev.lackluster.mihelper.ui.viewmodel.StackedMobileIconViewModel

val LocalStackedMobileViewModel = staticCompositionLocalOf<StackedMobileIconViewModel> {
    error("StackedMobileIconViewModel is not provided!")
}