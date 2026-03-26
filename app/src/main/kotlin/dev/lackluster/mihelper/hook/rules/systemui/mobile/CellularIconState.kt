package dev.lackluster.mihelper.hook.rules.systemui.mobile

sealed class CellularIconState {
    object None : CellularIconState()

    data class StackedSignal(
        val sim1Level: Int,
        val sim2Level: Int,
        val netType: String
    ) : CellularIconState()

    data class StandaloneNetType(
        val netType: String
    ) : CellularIconState()

    data class SingleSignal(
        val subId: Int,
        val level: Int,
        val netType: String
    ) : CellularIconState()
}