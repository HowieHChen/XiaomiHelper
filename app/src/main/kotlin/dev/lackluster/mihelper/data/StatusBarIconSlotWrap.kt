package dev.lackluster.mihelper.data

data class StatusBarIconSlotWrap(
    val slot: String,
    val iconResId: Int,
    val labelResId: Int = 0,
)