package dev.lackluster.mihelper.hook.rules.systemui.mobile

import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.rules.systemui.compat.FlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet

object CellularIconInteractor {
    private var isStarted = false

    private val showStackedSignal by lazy { Preferences.SystemUI.StatusBar.StackedMobile.STACKED_MOBILE_ICON.get() != 4 }
    private val showStandaloneType by lazy { Preferences.SystemUI.StatusBar.StackedMobile.STACKED_MOBILE_TYPE.get() != 4 }
    private val showSingleSignalSIM1 by lazy { Preferences.SystemUI.StatusBar.StackedMobile.SINGLE_MOBILE_SIM1.get() in 1..3 }
    private val showSingleSignalSIM2 by lazy { Preferences.SystemUI.StatusBar.StackedMobile.SINGLE_MOBILE_SIM2.get() in 1..3 }

    private val hideStandaloneTypeWhenDisconnect by Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT.lazyGet()
    private val hideStandaloneTypeWhenWifi by Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI.lazyGet()
    private val showTypeOnStackedSignal by Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_STACKED.lazyGet()
    private val showTypeOnSingleSignal by Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE.lazyGet()
    private val showRoamingOnSmallType by Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ROAMING.lazyGet()

    val jobs = mutableListOf<Any?>()

    val isWifiAvailable = MutableStateFlowCompat(false)
    val isAirplaneMode = MutableStateFlowCompat(false)
    val defaultDataSubId = MutableStateFlowCompat(-1)

    val sim1ConnectionInfo = MutableStateFlowCompat(defSimConnectionInfo)
    val sim2ConnectionInfo = MutableStateFlowCompat(defSimConnectionInfo)

    val proxySim1Signal = MutableStateFlowCompat<CellularIconState>(CellularIconState.None)
    val proxySim2Signal = MutableStateFlowCompat<CellularIconState>(CellularIconState.None)
    val proxyStackedSignal = MutableStateFlowCompat<CellularIconState>(CellularIconState.None)
    val proxyStandaloneNetType = MutableStateFlowCompat<CellularIconState>(CellularIconState.None)

    fun start(coroutineScope: Any) {
        if (isStarted) return
        isStarted = true
        FlowCompat.combineFlows(
            scope = coroutineScope,
            src1 = sim1ConnectionInfo,  defValue1 = defSimConnectionInfo,
            src2 = sim2ConnectionInfo,  defValue2 = defSimConnectionInfo,
            src3 = isAirplaneMode,      defValue3 = false,
            src4 = defaultDataSubId,    defValue4 = -1,
            dst = proxyStackedSignal,
        ) { sim1, sim2, airplane, defDataSubId ->
            if (!showStackedSignal) {
                CellularIconState.None
            } else if (airplane || (sim1.signalLevel.isAbsent && sim2.signalLevel.isAbsent)) {
                CellularIconState.None
            } else if (sim1.signalLevel.isAbsent) {
                sim2.toSingleSignal(showTypeOnStackedSignal, showRoamingOnSmallType)
            } else if (sim2.signalLevel.isAbsent) {
                sim1.toSingleSignal(showTypeOnStackedSignal, showRoamingOnSmallType)
            } else {
                createDualStackedSignal(sim1, sim2, defDataSubId, showTypeOnStackedSignal, showRoamingOnSmallType)
            }
        }.let { jobs.addAll(it) }
        FlowCompat.combineFlows(
            scope = coroutineScope,
            src1 = sim1ConnectionInfo,  defValue1 = defSimConnectionInfo,
            src2 = sim2ConnectionInfo,  defValue2 = defSimConnectionInfo,
            src3 = isAirplaneMode,      defValue3 = false,
            src4 = isWifiAvailable,     defValue4 = false,
            src5 = defaultDataSubId,    defValue5 = -1,
            dst = proxyStandaloneNetType,
        ) { sim1, sim2, airplane, wifi, defDataSubId ->
            if (!showStandaloneType) {
                return@combineFlows CellularIconState.None
            }
            if (airplane || (sim1.signalLevel.isAbsent && sim2.signalLevel.isAbsent)) {
                return@combineFlows CellularIconState.None
            }
            if (wifi && hideStandaloneTypeWhenWifi) {
                return@combineFlows CellularIconState.None
            }
            val dataInfo = when (defDataSubId) {
                sim1.subId -> sim1
                sim2.subId -> sim2
                else -> null
            }
            if (dataInfo == null || dataInfo.networkType.isBlank()) {
                return@combineFlows CellularIconState.None
            }
            if (!dataInfo.isDataConnected && hideStandaloneTypeWhenDisconnect) {
                return@combineFlows CellularIconState.None
            }
            return@combineFlows CellularIconState.StandaloneNetType(dataInfo.networkType)
        }.let { jobs.addAll(it) }
        FlowCompat.combineFlows(
            scope = coroutineScope,
            src1 = sim1ConnectionInfo,  defValue1 = defSimConnectionInfo,
            src2 = isAirplaneMode,      defValue2 = false,
            dst = proxySim1Signal
        ) { sim, airplane ->
            if (!showSingleSignalSIM1) {
                CellularIconState.None
            } else if (airplane || sim.signalLevel.isAbsent) {
                CellularIconState.None
            } else {
                sim.toSingleSignal(showTypeOnSingleSignal)
            }
        }.let { jobs.addAll(it) }
        FlowCompat.combineFlows(
            scope = coroutineScope,
            src1 = sim2ConnectionInfo,  defValue1 = defSimConnectionInfo,
            src2 = isAirplaneMode,      defValue2 = false,
            dst = proxySim2Signal
        ) { sim, airplane ->
            if (!showSingleSignalSIM2) {
                CellularIconState.None
            } else if (airplane || sim.signalLevel.isAbsent) {
                CellularIconState.None
            } else {
                sim.toSingleSignal(showTypeOnSingleSignal)
            }
        }.let { jobs.addAll(it) }
    }
}