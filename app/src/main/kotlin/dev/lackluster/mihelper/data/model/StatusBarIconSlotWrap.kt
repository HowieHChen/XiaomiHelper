package dev.lackluster.mihelper.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class StatusBarIconSlotWrap(
    val slot: String,
    @DrawableRes val iconResId: Int,
    @StringRes val labelResId: Int = 0,
)