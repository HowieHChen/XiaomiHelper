package dev.lackluster.mihelper.hook.rules.systemui.mobile

import java.util.concurrent.ConcurrentHashMap

fun SimConnectionInfo.toSingleSignal(
    showNetType: Boolean = false,
    showRoamingR: Boolean = true,
): CellularIconState.SingleSignal {
    return CellularIconState.SingleSignal(
        subId = this.subId,
        level = this.signalLevel.value,
        netType = if (showNetType) this.getDisplayNetType(showRoamingR) else ""
    )
}

fun createDualStackedSignal(
    info1: SimConnectionInfo,
    info2: SimConnectionInfo,
    dataSubId: Int,
    showNetType: Boolean = false,
    showRoamingR: Boolean = true,
): CellularIconState.StackedSignal {
    return CellularIconState.StackedSignal(
        sim1Level = info1.signalLevel.value,
        sim2Level = info2.signalLevel.value,
        netType = if (showNetType) {
            when (dataSubId) {
                info1.subId -> info1.getDisplayNetType(showRoamingR)
                info2.subId -> info2.getDisplayNetType(showRoamingR)
                else -> ""
            }
        } else ""
    )
}

fun SimConnectionInfo.getDisplayNetType(showRoaming: Boolean = true): String {
    if (networkType.isBlank()) return ""
    return if (isRoaming && showRoaming) {
        roamingStringCache.getOrPut(networkType) {
            "R$networkType"
        }
    } else networkType
}

private val roamingStringCache = ConcurrentHashMap<String, String>(16)