package dev.lackluster.mihelper.hook.rules.systemui.mobile

data class SimConnectionInfo(
    val subId: Int,                                     // 卡槽ID
    val signalLevel: SignalLevel = SignalLevel.ABSENT,  // 信号等级
    val networkType: String = "",                       // 处理后的网络类型； 5G 4G 等
    val isRoaming: Boolean = false,                     // 是否漫游
    val isDataConnected: Boolean = false,               // 数据已连接
)

val defSimConnectionInfo = SimConnectionInfo(subId = -1)
