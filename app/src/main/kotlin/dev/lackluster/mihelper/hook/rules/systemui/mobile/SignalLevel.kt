package dev.lackluster.mihelper.hook.rules.systemui.mobile

@JvmInline
value class SignalLevel(val value: Int) {
    init {
        require(value in -2..4) { "Signal level must be between -2 and 4, but received: $value" }
    }

    val isAbsent: Boolean get() = value == -2
    val isNoService: Boolean get() = value == -1
    val hasValidSignal: Boolean get() = value >= 0

    companion object {
        val ABSENT = SignalLevel(-2)
        val NO_SERVICE = SignalLevel(-1)
        val MAX_LEVEL = SignalLevel(4)
    }
}