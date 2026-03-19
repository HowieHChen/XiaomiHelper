package dev.lackluster.mihelper.hook.rules.systemui.mobile

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzStatusBarIconControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow.collectFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.IconControllerCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.TripleCompat
import dev.lackluster.mihelper.utils.HostExecutor
import dev.lackluster.mihelper.utils.Prefs
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

object StackedMobileIcon : YukiBaseHooker() {
    private val enabled = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.ENABLE_STACKED_MOBILE_ICON, false)
    // MobileType
    private val typeHideWhenDisconnect = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_HIDE_DISCONNECT, true)
    private val typeHideWhenWifi = Prefs.getBoolean(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_HIDE_WIFI, true)

    private val simCacheMap = HashMap<Int, SimPipelineCache>()
    private val flowJobs = mutableListOf<Any?>()

    private var relayJobSim1Signal: Any? = null
    private var relayJobSim1NetType: Any? = null
    private var relayJobSim2Signal: Any? = null
    private var relayJobSim2NetType: Any? = null

    private val proxySim1Signal by lazy { MutableStateFlowCompat(-2) } // -2表示未插卡，-1无服务，0-4正常
    private val proxySim1NetType by lazy { MutableStateFlowCompat("") }

    private val proxySim2Signal by lazy { MutableStateFlowCompat(-2) } // -2表示未插卡，-1无服务，0-4正常
    private val proxySim2NetType by lazy { MutableStateFlowCompat("") }

    private val proxyStackedSignal by lazy { MutableStateFlowCompat("") } // 信号图标 Key
    private val proxyStackedNetType by lazy { MutableStateFlowCompat("") } // 网络类型文本

    val clzMiuiMobileIconVMImpl by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MiuiMobileIconVMImpl".toClassOrNull()
    }
    val fldShowName by lazy {
        clzMiuiMobileIconVMImpl?.resolve()?.firstFieldOrNull {
            name = "showName"
        }?.self?.apply { makeAccessible() }
    }
    val fldIconInteractor by lazy {
        clzMiuiMobileIconVMImpl?.resolve()?.firstFieldOrNull {
            name = "iconInteractor"
        }?.self?.apply { makeAccessible() }
    }
    val fldOriginIconInteractor by lazy {
        clzMiuiMobileIconVMImpl?.resolve()?.firstFieldOrNull {
            name = "originIconInteractor"
        }?.self?.apply { makeAccessible() }
    }

    private val clzMobileIconInteractor by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconInteractor".toClassOrNull()
    }
    private val metGetSignalLevelIcon by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "getSignalLevelIcon"
        }?.self?.apply { makeAccessible() }
    }
    private val metIsDataConnected by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "isDataConnected"
        }?.self?.apply { makeAccessible() }
    }
    private val metIsInService by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "isInService"
        }?.self?.apply { makeAccessible() }
    }

    private val fldIsAirplaneMode by lazy {
        "com.android.systemui.statusbar.pipeline.airplane.domain.interactor.AirplaneModeInteractor".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "isAirplaneMode"
            }?.self?.apply { makeAccessible() }
        }
    }

    private val fldWifiAvailable by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MiuiMobileIconInteractorImpl".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "wifiAvailable"
            }?.self?.apply { makeAccessible() }
        }
    }

    private val fldSubscriptionId by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconViewModel".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "subscriptionId"
            }?.self?.apply { makeAccessible() }
        }
    }

    private val clzSignalIconModelCellular by lazy {
        $$"com.android.systemui.statusbar.pipeline.mobile.domain.model.SignalIconModel$Cellular".toClassOrNull()
    }
    private val fldLevel by lazy {
        clzSignalIconModelCellular?.resolve()?.firstFieldOrNull {
            name = "level"
        }?.self?.apply { makeAccessible() }
    }
    private val fldNumberOfLevels by lazy {
        clzSignalIconModelCellular?.resolve()?.firstFieldOrNull {
            name = "numberOfLevels"
        }?.self?.apply { makeAccessible() }
    }

    private val fldContext by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstFieldOrNull {
            name = "mContext"
        }?.self?.apply { makeAccessible() }
    }
    private val fldStatusBarIconList by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstFieldOrNull {
            name = "mStatusBarIconList"
        }?.self?.apply { makeAccessible() }
    }
    private val metHandleSet by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstMethodOrNull {
            name = "handleSet"
        }?.self?.apply { makeAccessible() }
    }

    private val metGetIconHolder by lazy {
        "com.android.systemui.statusbar.phone.ui.StatusBarIconList".toClassOrNull()?.let {
            it.resolve().firstMethodOrNull {
                name = "getIconHolder"
                parameters(Int::class, String::class)
            }?.self?.apply { makeAccessible() }
        }
    }

    private val fldIcon by lazy {
        "com.android.systemui.statusbar.phone.StatusBarIconHolder".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "icon"
            }?.self?.apply { makeAccessible() }
        }
    }

    private val clzStatusBarIcon by lazy {
        "com.android.internal.statusbar.StatusBarIcon".toClassOrNull()
    }
    private val fldRealIcon by lazy {
        clzStatusBarIcon?.resolve()?.firstFieldOrNull {
            name = "icon"
        }?.self?.apply { makeAccessible() }
    }
    private val fldPkg by lazy {
        clzStatusBarIcon?.resolve()?.firstFieldOrNull {
            name = "pkg"
        }?.self?.apply { makeAccessible() }
    }

    override fun onHook() {
        if (!enabled) return
        // 强制着色来自动反色
        "com.android.systemui.statusbar.StatusBarIconView".toClassOrNull()?.apply {
            val fldSlot = resolve().firstFieldOrNull {
                name = "mSlot"
            }?.self?.apply { makeAccessible() }
            val metSetDecorColor = resolve().firstMethodOrNull {
                name = "setDecorColor"
                parameters(Int::class)
            }?.self?.apply { makeAccessible() }
            val metGetTint = "com.android.systemui.statusbar.DarkIconDispatcherExt".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "getTint"
                parameterCount = 3
                modifiers(Modifiers.STATIC)
            }
            resolve().firstMethodOrNull {
                name = "updateLightDarkTint"
            }?.hook {
                after {
                    val slot = fldSlot?.get(this.instance) as? String ?: return@after
                    if (
                        slot == Constants.IconSlots.STACKED_MOBILE_TYPE ||
                        slot == Constants.IconSlots.STACKED_MOBILE_ICON
                    ) {
                        val iconView = this.instance<ImageView>()
                        metGetTint?.invoke<Int>(
                            this.args(0).list<Any?>(),
                            iconView,
                            this.args(2).int()
                        )?.let { tint ->
//                            iconView.imageTintList = ColorStateList.valueOf(tint)
                            iconView.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            metSetDecorColor?.invoke(iconView, tint)
                        }
                    }
                }
            }
        }
        // 刷新图标的 Flow
        "com.android.systemui.statusbar.pipeline.mobile.ui.MobileUiAdapter".toClassOrNull()?.apply {
            val fldIconController = resolve().firstFieldOrNull {
                name = "iconController"
            }?.self?.apply { makeAccessible() }
            val fldScope = resolve().firstFieldOrNull {
                name = "scope"
            }?.self?.apply { makeAccessible() }
            resolve().firstMethodOrNull {
                name = "start"
            }?.hook {
                after {
                    val iconController = fldIconController?.get(this.instance) ?: return@after
                    val coroutineScope = fldScope?.get(this.instance) ?: return@after
                    val context = fldContext?.get(iconController) as? Context ?: return@after
                    // 刷新信号图标
                    val renderSignalUI = { mobileIconKey: String? ->
                        val icon = mobileIconKey?.takeIf { it.isNotEmpty() }?.let { key ->
                            StackedMobileIconCache.getSignalIcon(context, key)
                        }
                        if (icon != null) {
                            updateMobileIcon(iconController, Constants.IconSlots.STACKED_MOBILE_ICON, icon)
                            IconControllerCompat.setIconVisibility(
                                iconController,
                                Constants.IconSlots.STACKED_MOBILE_ICON,
                                true
                            )
                        } else {
                            IconControllerCompat.setIconVisibility(
                                iconController,
                                Constants.IconSlots.STACKED_MOBILE_ICON,
                                false
                            )
                        }
                    }
                    // 刷新网络类型
                    val renderNetTypeUI = { mobileTypeStr: String? ->
                        val icon = mobileTypeStr?.takeIf { it.isNotEmpty() }?.let { key ->
                            StackedMobileIconCache.getTypeIcon(context, key)
                        }
                        if (icon != null) {
                            updateMobileIcon(iconController, Constants.IconSlots.STACKED_MOBILE_TYPE, icon)
                            IconControllerCompat.setIconVisibility(
                                iconController,
                                Constants.IconSlots.STACKED_MOBILE_TYPE,
                                true
                            )
                        } else {
                            IconControllerCompat.setIconVisibility(
                                iconController,
                                Constants.IconSlots.STACKED_MOBILE_TYPE,
                                false
                            )
                        }
                    }
                    // 初始化图标缓存
                    HostExecutor.execute(
                        tag = "PRELOAD_STACKED_MOBILE_SVG",
                        backgroundTask = {
                            StackedMobileIconCache.preload(context.applicationContext) // 随便返回一个非 null 的值，触发 onResult
                        },
                        runOnMain = true, // 解析完切回主线程
                        onResult = {
                            // 数据在图标就绪前到达，补一次刷新
                            renderSignalUI(proxyStackedSignal.getValue())
                            renderNetTypeUI(proxyStackedNetType.getValue())
                        }
                    )
                    proxyStackedSignal.collectFlow(coroutineScope) { mobileIconKey ->
                        YLog.info("proxyStackedSignal $mobileIconKey")
                        renderSignalUI(mobileIconKey)
                    }.let {
                        flowJobs.add(it)
                    }
                    proxyStackedNetType.collectFlow(coroutineScope) { mobileTypeStr ->
                        YLog.info("proxyStackedNetType $mobileTypeStr")
                        renderNetTypeUI(mobileTypeStr)
                    }.let {
                        flowJobs.add(it)
                    }
                }
            }
        }
        // 监听数据的 Flow
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconsViewModel".toClassOrNull()?.apply {
            val fldAirplaneModeInteractor = resolve().firstFieldOrNull {
                name = "airplaneModeInteractor"
            }
            val fldInteractor = resolve().firstFieldOrNull {
                name = "interactor"
            }
            val fldMobileSubViewModels = resolve().firstFieldOrNull {
                name = "mobileSubViewModels"
            }
            val fldReuseCache = resolve().firstFieldOrNull {
                name = "reuseCache"
            }
            val metGetDefaultDataSubId = "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconsInteractor".toClassOrNull()?.let {
                it.resolve().firstMethodOrNull {
                    name { it1 ->
                        it1.startsWith("getDefaultDataSubId")
                    }
                }?.self?.apply { makeAccessible() }
            }
            resolve().firstConstructor().hook {
                after {
                    val coroutineScope =
                        this.args.firstOrNull { CommonClassUtils.clzCoroutineScope?.isInstance(it) == true } ?: return@after
                    val reuseCache = fldReuseCache?.copy()?.of(this.instance)?.get<ConcurrentHashMap<Int, Any>>() ?: return@after
                    val mobileSubViewModels =
                        fldMobileSubViewModels?.copy()?.of(this.instance)?.get()?.let {
                            ReadonlyStateFlowCompat<List<Any?>>().of(it)
                        } ?: return@after
                    val isAirplaneMode = fldAirplaneModeInteractor?.copy()?.of(this.instance)?.get()?.let {
                        fldIsAirplaneMode?.get(it)?.let { it1 ->
                            ReadonlyStateFlowCompat<Boolean>().of(it1)
                        }
                    } ?: return@after
                    val defaultDataSubId = fldInteractor?.copy()?.of(this.instance)?.get()?.let {
                        metGetDefaultDataSubId?.invoke(it)
                    } ?: return@after
                    // 双卡数据合并规则
                    Flow.combineFlows(
                        scope = coroutineScope,
                        src1 = proxySim1Signal, defValue1 = -2,
                        src2 = proxySim2Signal, defValue2 = -2,
                        src3 = isAirplaneMode, defValue3 = false,
                        dst = proxyStackedSignal
                    ) { signal1, signal2, airplaneMode ->
                        if (airplaneMode || (signal1 == -2 && signal2 == -2)) {
                            ""
                        } else if (signal1 == -2) {
                            signal2.toString()
                        } else if (signal2 == -2) {
                            signal1.toString()
                        } else {
                            "${signal1}_${signal2}"
                        }
                    }.let {
                        flowJobs.addAll(it)
                    }
                    Flow.combineFlows(
                        scope = coroutineScope,
                        src1 = proxySim1NetType,
                        defValue1 = "",
                        src2 = proxySim2NetType,
                        defValue2 = "",
                        src3 = isAirplaneMode,
                        defValue3 = false,
                        dst = proxyStackedNetType
                    ) { type1, type2, airplaneMode ->
                        if (airplaneMode || (type1.isEmpty() && type2.isEmpty())) {
                            ""
                        } else {
                            type1.ifEmpty { type2 }
                        }
                    }.let {
                        flowJobs.addAll(it)
                    }
                    mobileSubViewModels.collectFlow(coroutineScope) { vms ->
                        val subIds = vms.mapNotNull {
                            it?.let { it1 -> fldSubscriptionId?.get(it1) } as? Int
                        }
                        YLog.info("subscriptionIdsFlow ${subIds.joinToString(",")} \nreuseCache" + reuseCache.keys.joinToString(", "))
                        // 1. 缓存清理：干掉已经被拔出的卡，释放内存
                        val iterator = simCacheMap.entries.iterator()
                        while (iterator.hasNext()) {
                            val entry = iterator.next()
                            if (!subIds.contains(entry.key)) {
                                entry.value.destroy() // 取消协程收集任务
                                iterator.remove()     // 从缓存池移除
                            }
                        }
                        // 2. 缓存创建：为新插入的卡建立独立流水线
                        subIds.forEach { subId ->
                            if (!simCacheMap.containsKey(subId)) {
                                // 通过接口拿到这块新卡的 Interactor
                                val miuiMobileIconVMImpl = reuseCache[subId]?.let { TripleCompat.getThird(it) } ?: return@forEach
                                val showName = fldShowName?.get(miuiMobileIconVMImpl) ?: return@forEach
                                val miuiInteractor = fldIconInteractor?.get(miuiMobileIconVMImpl) ?: return@forEach
                                val originInteractor = fldOriginIconInteractor?.get(miuiMobileIconVMImpl) ?: return@forEach
                                simCacheMap[subId] = SimPipelineCache(subId, coroutineScope, defaultDataSubId, showName, miuiInteractor, originInteractor)
                            }
                        }
                        // 3. 动态路由：把缓存池里的数据“接线”到固定的 Proxy 流
                        val subId1 = subIds.getOrNull(0)
                        val subId2 = subIds.getOrNull(1)

                        Flow.cancelJob(relayJobSim1Signal)
                        Flow.cancelJob(relayJobSim1NetType)
                        if (subId1 != null && simCacheMap[subId1] != null) {
                            val cache1 = simCacheMap[subId1]!!
                            relayJobSim1Signal = cache1.signalLevelResult.collectFlow(coroutineScope) { proxySim1Signal.setValue(it) }
                            relayJobSim1NetType = cache1.mobileTypeResult.collectFlow(coroutineScope) { proxySim1NetType.setValue(it) }
                        } else {
                            proxySim1Signal.setValue(-2)
                            proxySim1NetType.setValue("")
                        }
                        Flow.cancelJob(relayJobSim2Signal)
                        Flow.cancelJob(relayJobSim2NetType)
                        if (subId2 != null && simCacheMap[subId2] != null) {
                            val cache2 = simCacheMap[subId2]!!
                            relayJobSim2Signal = cache2.signalLevelResult.collectFlow(coroutineScope) { proxySim2Signal.setValue(it) }
                            relayJobSim2NetType = cache2.mobileTypeResult.collectFlow(coroutineScope) { proxySim2NetType.setValue(it) }
                        } else {
                            proxySim2Signal.setValue(-2)
                            proxySim2NetType.setValue("")
                        }
                    }.let {
                        flowJobs.add(it)
                    }
                }
            }
        }
    }

    private fun updateMobileIcon(iconController: Any, slot: String, newIcon: Icon) {
        try {
            val iconList = fldStatusBarIconList?.get(iconController)
            var holder = metGetIconHolder?.invoke(iconList, 0, slot)
            if (holder == null) {
                IconControllerCompat.setIcon(
                    iconController,
                    null,
                    slot,
                    ResourcesUtils.stat_sys_signal_0
                )
                holder = metGetIconHolder?.invoke(iconList, 0, slot)
            }

            if (holder != null) {
                val statusBarIcon = fldIcon?.get(holder) ?: return
                fldPkg?.set(statusBarIcon, BuildConfig.APPLICATION_ID)
                fldRealIcon?.set(statusBarIcon, newIcon)
                metHandleSet?.invoke(iconController, slot, holder)
            }
        } catch (e: Exception) {
            YLog.error("Failed to update optimized icon for slot: $slot", e)
        }
    }

    class SimPipelineCache(
        val subId: Int,
        coroutineScope: Any,
        defaultDataSubIdFlow: Any,
        mobileTypeNameFlow: Any,
        miuiInteractor: Any,
        originInteractor: Any,
    ) {
        val signalLevelResult = MutableStateFlowCompat(-2)  // -2: 未插卡; -1: 异常/无服务; 0-4: 重映射后的信号强度
        val mobileTypeResult = MutableStateFlowCompat("")   // 空串: 不显示; 非空串: 当前的网络类型文本

        private val jobs = mutableListOf<Any?>()

        init {
            val signalLevelIcon = metGetSignalLevelIcon?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Any?>().of(it) }
            val isInService = metIsInService?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            // 信号图标
            if (signalLevelIcon != null && isInService != null) {
                Flow.combineFlows(
                    scope = coroutineScope,
                    src1 = signalLevelIcon,
                    defValue1 = null,
                    src2 = isInService,
                    defValue2 = false,
                    dst = signalLevelResult
                ) { signalIconModel, inService ->
                    if (!inService || signalIconModel == null || clzSignalIconModelCellular?.isInstance(
                            signalIconModel
                        ) != true
                    ) {
                        return@combineFlows -1
                    }
                    val levelNow = fldLevel?.getInt(signalIconModel)
                    val levelAll = fldNumberOfLevels?.getInt(signalIconModel)
                    if (levelNow == null || levelAll == null) {
                        return@combineFlows -1
                    }
                    return@combineFlows if (levelNow <= 0 || levelAll <= 0) {
                        0
                    } else if (levelNow >= levelAll) {
                        4
                    } else {
                        ((levelNow * 4.0f) / levelAll).roundToInt()
                    }
                }.let {
                    jobs.addAll(it)
                }
            }
            val defaultDataSubId = defaultDataSubIdFlow.let { ReadonlyStateFlowCompat<Int>().of(it) }
            val isDataConnected = metIsDataConnected?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val wifiAvailable = fldWifiAvailable?.get(miuiInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val mobileTypeName = mobileTypeNameFlow.let { ReadonlyStateFlowCompat<String>().of(it) }
            // 网络类型
            if (isDataConnected != null && isInService != null && wifiAvailable != null) {
                Flow.combineFlows(
                    scope = coroutineScope,
                    src1 = defaultDataSubId,
                    defValue1 = -1,
                    src2 = isDataConnected,
                    defValue2 = false,
                    src3 = isInService,
                    defValue3 = false,
                    src4 = wifiAvailable,
                    defValue4 = false,
                    src5 = mobileTypeName,
                    defValue5 = "",
                    dst = mobileTypeResult
                ) { defDataSubId, connected, inService, wifi, typeName ->
                    YLog.info("subId $subId defDataSubId $defDataSubId connected $connected inService $inService wifi $wifi typeName $typeName")
                    if (defDataSubId != subId || !inService) {
                        return@combineFlows ""
                    }
                    if (
                        (!connected && typeHideWhenDisconnect) ||
                        (wifi && typeHideWhenWifi)
                    ) {
                        return@combineFlows ""
                    }
                    return@combineFlows typeName
                }.let {
                    jobs.addAll(it)
                }
            }
        }

        fun destroy() {
            jobs.forEach { job ->
                Flow.cancelJob(job)
            }
            jobs.clear()
        }
    }
}