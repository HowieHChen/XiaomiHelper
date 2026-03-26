package dev.lackluster.mihelper.ui.model

data class StackedMobileRenderKey(
    val svgHash: Int,
    val isStacked: Boolean,
    val alphaFg: Float,
    val alphaBg: Float,
    val alphaError: Float,
)
