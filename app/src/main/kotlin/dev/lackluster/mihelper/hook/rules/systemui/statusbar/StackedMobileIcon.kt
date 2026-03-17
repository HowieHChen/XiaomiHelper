package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.text.TextPaint
import android.view.View
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzCoroutineScope
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow.cancelJob
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow.collectFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.Flow.combineFlows
import dev.lackluster.mihelper.hook.rules.systemui.compat.IconControllerCompat.setIcon
import dev.lackluster.mihelper.hook.rules.systemui.compat.IconControllerCompat.setIconVisibility
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.TripleCompat
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.StackedMobileIconUtils
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import androidx.core.graphics.createBitmap
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_signal_0
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzStatusBarIconControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.getTypeface
import dev.lackluster.mihelper.utils.HostExecutor

object StackedMobileIcon : YukiBaseHooker() {
    private val enabled = Prefs.getBoolean(IconTuner.ENABLE_STACKED_MOBILE_ICON, false)
    // MobileType
    private val valueTypeFW = Prefs.getInt(Pref.Key.SystemUI.FontWeight.STACKED_MOBILE_TYPE_VAL, 400)
    private val modifyTypeFW =
        Prefs.getBoolean(Pref.Key.SystemUI.FontWeight.STACKED_MOBILE_TYPE, false) && valueTypeFW in 1..1000
    private val valueTypeSize = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_SIZE, 14.0f)
    private val typeHideWhenDisconnect = Prefs.getBoolean(IconTuner.STACKED_MOBILE_TYPE_HIDE_DISCONNECT, true)
    private val typeHideWhenWifi = Prefs.getBoolean(IconTuner.STACKED_MOBILE_TYPE_HIDE_WIFI, true)
    private val valuePaddingStart = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_PADDING_START_VAL, 2.0f)
    private val valuePaddingEnd = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_PADDING_END_VAL, 2.0f)
    private val typefaceTypeFW by lazy {
        if (modifyTypeFW) getTypeface(valueTypeFW)
        else Typeface.DEFAULT_BOLD
    }

    private val simCacheMap = HashMap<Int, SimPipelineCache>()
    private val flowJobs = mutableListOf<Any?>()

    private var relayJobSim1Signal: Any? = null
    private var relayJobSim1NetType: Any? = null
    private var relayJobSim2Signal: Any? = null
    private var relayJobSim2NetType: Any? = null

    private val proxySim1Signal by lazy {
        MutableStateFlowCompat(-2)
    } // -2表示未插卡，-1无服务，0-4正常
    private val proxySim1NetType by lazy {
        MutableStateFlowCompat("")
    }

    private val proxySim2Signal by lazy {
        MutableStateFlowCompat(-2)
    } // -2表示未插卡，-1无服务，0-4正常
    private val proxySim2NetType by lazy {
        MutableStateFlowCompat("")
    }

    private val proxyStackedSignal by lazy {
        MutableStateFlowCompat("")
    }
    private val proxyStackedNetType by lazy {
        MutableStateFlowCompat("")
    }

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
    private val metGetMobileIsDefault by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "getMobileIsDefault"
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

    private val clzAirplaneModeInteractor by lazy {
        "com.android.systemui.statusbar.pipeline.airplane.domain.interactor.AirplaneModeInteractor".toClassOrNull()
    }
    private val fldIsAirplaneMode by lazy {
        clzAirplaneModeInteractor?.resolve()?.firstFieldOrNull {
            name = "isAirplaneMode"
        }?.self?.apply { makeAccessible() }
    }

    val clzMiuiMobileIconInteractorImpl by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MiuiMobileIconInteractorImpl".toClassOrNull()
    }
    val fldWifiAvailable by lazy {
        clzMiuiMobileIconInteractorImpl?.resolve()?.firstFieldOrNull {
            name = "wifiAvailable"
        }?.self?.apply { makeAccessible() }
    }

    val clzMobileIconViewModel by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconViewModel".toClassOrNull()
    }
    val fldSubscriptionId by lazy {
        clzMobileIconViewModel?.resolve()?.firstFieldOrNull {
            name = "subscriptionId"
        }?.self?.apply { makeAccessible() }
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

    private val clzStatusBarIconList by lazy {
        "com.android.systemui.statusbar.phone.ui.StatusBarIconList".toClassOrNull()
    }
    private val metGetIconHolder by lazy {
        clzStatusBarIconList?.resolve()?.firstMethodOrNull {
            name = "getIconHolder"
            parameters(Int::class, String::class)
        }?.self?.apply { makeAccessible() }
    }

    private val fldIcon by lazy {
        "com.android.systemui.statusbar.phone.StatusBarIconHolder".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "icon"
        }?.self?.apply { makeAccessible() }
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

                    val renderSignalUI = { mobileIconKey: String? ->
                        val icon = mobileIconKey?.takeIf { it.isNotEmpty() }?.let { key ->
                            StackedMobileIconCache.getSignalIcon(context, key)
                        }
                        if (icon != null) {
                            updateMobileIcon(iconController, Constants.IconSlots.STACKED_MOBILE_ICON, icon)
                            setIconVisibility(iconController, Constants.IconSlots.STACKED_MOBILE_ICON, true)
                        } else {
                            setIconVisibility(iconController, Constants.IconSlots.STACKED_MOBILE_ICON, false)
                        }
                    }

                    val renderNetTypeUI = { mobileTypeStr: String? ->
                        val icon = mobileTypeStr?.takeIf { it.isNotEmpty() }?.let { key ->
                            StackedMobileIconCache.getTypeIcon(context, key)
                        }
                        if (icon != null) {
                            updateMobileIcon(iconController, Constants.IconSlots.STACKED_MOBILE_TYPE, icon)
                            setIconVisibility(iconController, Constants.IconSlots.STACKED_MOBILE_TYPE, true)
                        } else {
                            setIconVisibility(iconController, Constants.IconSlots.STACKED_MOBILE_TYPE, false)
                        }
                    }

                    HostExecutor.execute(
                        tag = "PRELOAD_STACKED_MOBILE_SVG",
                        backgroundTask = {
                            StackedMobileIconCache.preload() // 随便返回一个非 null 的值，触发 onResult
                        },
                        runOnMain = true, // 解析完切回主线程
                        onResult = {
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
            val metGetDefaultDataSubId = "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconsInteractor".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name {
                    it.startsWith("getDefaultDataSubId")
                }
            }?.self?.apply { makeAccessible() }
            resolve().firstConstructor().hook {
                after {
                    val coroutineScope =
                        this.args.firstOrNull { clzCoroutineScope?.isInstance(it) == true } ?: return@after
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
                    // 合并规则
                    combineFlows(
                        scope = coroutineScope,
                        src1 = proxySim1Signal,
                        defValue1 = -2,
                        src2 = proxySim2Signal,
                        defValue2 = -2,
                        src3 = isAirplaneMode,
                        defValue3 = false,
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
                        flowJobs.add(it.first)
                        flowJobs.add(it.second)
                        flowJobs.add(it.third)
                    }
                    combineFlows(
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
                        flowJobs.add(it.first)
                        flowJobs.add(it.second)
                        flowJobs.add(it.third)
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

                        cancelJob(relayJobSim1Signal) // 先切断旧的转播线
                        cancelJob(relayJobSim1NetType) // 先切断旧的转播线
                        if (subId1 != null && simCacheMap[subId1] != null) {
                            val cache1 = simCacheMap[subId1]!!
                            // 用一个简单的收集器，把 cache1 的数据实时搬运给 proxySim1
                            relayJobSim1Signal = cache1.signalLevelResult.collectFlow(coroutineScope) { proxySim1Signal.setValue(it) }
                            relayJobSim1NetType = cache1.mobileTypeResult.collectFlow(coroutineScope) { proxySim1NetType.setValue(it) }
                        } else {
                            // 卡 1 位置没卡，通知下游
                            proxySim1Signal.setValue(-2)
                            proxySim1NetType.setValue("")
                        }

                        cancelJob(relayJobSim2Signal)
                        cancelJob(relayJobSim2NetType)
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
                setIcon(iconController, null, slot, stat_sys_signal_0)
                holder = metGetIconHolder?.invoke(iconList, 0, slot)
            }

            if (holder != null) {
                val statusBarIcon = fldIcon?.get(holder) ?: return
                fldPkg?.set(statusBarIcon, BuildConfig.APPLICATION_ID)
//                statusBarIcon.setAdditionalInstanceField(KEY_STACKED_MOBILE, true)
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
        // 存储这个 subId 计算出的最终结果
        val signalLevelResult = MutableStateFlowCompat(-2)  // -2: 未插卡; -1: 异常/无服务; 0-4: 重映射后的信号强度
        val mobileTypeResult = MutableStateFlowCompat("")   // 空串: 不显示; 非空串: 当前的网络类型文本

        private val jobs = mutableListOf<Any?>()

        init {
            val signalLevelIcon = metGetSignalLevelIcon?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Any?>().of(it) }
            val isInService = metIsInService?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            // 信号图标
            if (signalLevelIcon != null && isInService != null) {
                combineFlows(
                    scope = coroutineScope,
                    src1 = signalLevelIcon,
                    defValue1 = null,
                    src2 = isInService,
                    defValue2 = false,
                    dst = signalLevelResult
                ) { signalIconModel, inService ->
                    if (!inService || signalIconModel == null || clzSignalIconModelCellular?.isInstance(signalIconModel) != true) {
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
                    jobs.add(it.first)
                    jobs.add(it.second)
                }
            }
//            val mobileIsDefault = metGetMobileIsDefault?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val defaultDataSubId = defaultDataSubIdFlow.let { ReadonlyStateFlowCompat<Int>().of(it) }
            val isDataConnected = metIsDataConnected?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val wifiAvailable = fldWifiAvailable?.get(miuiInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val mobileTypeName = mobileTypeNameFlow.let { ReadonlyStateFlowCompat<String>().of(it) }
            // 网络类型
            if (isDataConnected != null && isInService != null && wifiAvailable != null) {
                combineFlows(
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

        // 当这张卡被拔出时调用
        fun destroy() {
            jobs.forEach { job ->
                cancelJob(job)
            }
            jobs.clear()
        }
    }

    object StackedMobileIconCache {
        private const val ICON_HEIGHT_DP = 20

        @Volatile
        var isPreloaded = false // 对外公开这个状态
            private set

        // L1: 矢量层，不受分辨率影响，永久缓存 (占用内存极小)
        private val pictureCache = HashMap<String, Picture>(42)

        // L2: 光栅化层，受分辨率影响，存 Icon
        private val signalIconCache = HashMap<String, Icon>(42)

        private val typeIconCache = HashMap<String, Icon>(16)

        // 记录生成当前 L2 缓存时的系统 DPI
        private var currentDpi = -1

        fun preload(): Boolean {
            if (isPreloaded) return false
            synchronized(this) {
                if (isPreloaded) return false
                val singleMobileSVGString = when (
                    Prefs.getInt(IconTuner.STACKED_MOBILE_ICON_SVG_SINGLE, 0)
                ) {
                    0 -> Constants.STACKED_MOBILE_ICON_SINGLE_MIUI
                    1 -> Constants.STACKED_MOBILE_ICON_SINGLE_IOS
                    else -> Prefs.getString(
                        IconTuner.STACKED_MOBILE_ICON_SVG_SINGLE_VAL,
                        Constants.STACKED_MOBILE_ICON_SINGLE_MIUI
                    ).takeIf { it.isNotBlank() } ?: Constants.STACKED_MOBILE_ICON_SINGLE_MIUI
                }
                val stackedMobileSVGString = when (
                    Prefs.getInt(IconTuner.STACKED_MOBILE_ICON_SVG_STACKED, 0)
                ) {
                    0 -> Constants.STACKED_MOBILE_ICON_STACKED_MIUI
                    1 -> Constants.STACKED_MOBILE_ICON_STACKED_IOS
                    else -> Prefs.getString(
                        IconTuner.STACKED_MOBILE_ICON_SVG_STACKED_VAL,
                        Constants.STACKED_MOBILE_ICON_STACKED_MIUI
                    ).takeIf { it.isNotBlank() } ?: Constants.STACKED_MOBILE_ICON_STACKED_MIUI
                }
                val alphaFilled = Prefs.getFloat(IconTuner.STACKED_MOBILE_ICON_ALPHA_FG, 1.0f)
                val alphaBackground = Prefs.getFloat(IconTuner.STACKED_MOBILE_ICON_ALPHA_BG, 0.4f)
                val alphaError = Prefs.getFloat(IconTuner.STACKED_MOBILE_ICON_ALPHA_ERROR, 0.2f)
                val done1 = StackedMobileIconUtils.generateSingleSignalPictures(
                    singleMobileSVGString = singleMobileSVGString,
                    pictureCache = pictureCache,
                    alphaFilled = alphaFilled,
                    alphaBackground = alphaBackground,
                    alphaError = alphaError
                )
                val done2 = StackedMobileIconUtils.generateStackedSignalPictures(
                    stackedMobileSVGString = stackedMobileSVGString,
                    pictureCache = pictureCache,
                    alphaFilled = alphaFilled,
                    alphaBackground = alphaBackground,
                    alphaError = alphaError,
                )
                isPreloaded = true
                return done1 && done2
            }
        }

        fun getSignalIcon(context: Context, key: String): Icon? {
            if (!isPreloaded) {
                return null
            }

            val displayMetrics = context.resources.displayMetrics
            val displayDpi = displayMetrics.densityDpi
            if (displayDpi != currentDpi) {
                signalIconCache.clear()
                typeIconCache.clear()
                currentDpi = displayDpi
            }

            signalIconCache[key]?.let { return it }

            val picture = pictureCache[key] ?: return null

            val density = displayMetrics.density
            val targetHeightPx = (ICON_HEIGHT_DP * density).toInt()
            val aspectRatio = picture.width.toFloat() / picture.height.toFloat()
            val targetWidthPx = (targetHeightPx * aspectRatio).toInt()

            if (targetWidthPx <= 0 || targetHeightPx <= 0) return null

            val bitmap = createBitmap(targetWidthPx, targetHeightPx)
            val canvas = Canvas(bitmap)
            val scaleX = targetWidthPx.toFloat() / picture.width.toFloat()
            val scaleY = targetHeightPx.toFloat() / picture.height.toFloat()
            canvas.scale(scaleX, scaleY)
            canvas.drawPicture(picture)

            val icon = Icon.createWithBitmap(bitmap)
            signalIconCache[key] = icon

            return icon
        }

        fun getTypeIcon(context: Context, type: String): Icon? {
            if (!isPreloaded) {
                return null
            }

            val displayMetrics = context.resources.displayMetrics
            val displayDpi = displayMetrics.densityDpi
            val density = displayMetrics.density
            if (displayDpi != currentDpi) {
                signalIconCache.clear()
                typeIconCache.clear()
                currentDpi = displayDpi
            }

            typeIconCache[type]?.let { return it }

            // 4. 配置抗锯齿文本画笔 (TextPaint)
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE // 必须是纯白，供系统层进行深浅色 Tint
                textSize = valueTypeSize * density
                textAlign = Paint.Align.CENTER // 以中心坐标为基准绘制
                typeface = typefaceTypeFW
            }

            // 5. 测量排版尺寸
            val textWidth = textPaint.measureText(type)
            val paddingStartPx = valuePaddingStart * density
            val paddingEndPx = valuePaddingEnd * density
            val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
            val actualPaddingLeftPx = if (isRtl) paddingEndPx else paddingStartPx
            val actualPaddingRightPx = if (isRtl) paddingStartPx else paddingEndPx

            // 文本的实际物理高度 (从最高点到底部下沉点)
            val fontMetrics = textPaint.fontMetrics

            // 图标的最终宽高 (向上取整防止边缘被裁切)
            val bitmapWidth = (textWidth + actualPaddingLeftPx + actualPaddingRightPx).toInt()
            val bitmapHeight = (ICON_HEIGHT_DP * density).toInt()

            if (bitmapWidth <= 0 || bitmapHeight <= 0) return null

            // 6. 创建透明底色的 Bitmap
            val bitmap = createBitmap(bitmapWidth, bitmapHeight)
            val canvas = Canvas(bitmap)

            // 7. 计算完美的几何中心与垂直居中基线
            val textCenterX = actualPaddingLeftPx + (textWidth / 2f)
            val centerY = bitmapHeight / 2f
            // Baseline 公式：中心点 Y 减去 (上浮与下沉的平均偏差)
            val baselineY = centerY - (fontMetrics.descent + fontMetrics.ascent) / 2f

            // 8. 绘制文本
            canvas.drawText(type, textCenterX, baselineY, textPaint)

            // 9. 包装并缓存
            val icon = Icon.createWithBitmap(bitmap)
            typeIconCache[type] = icon

            return icon
        }
    }
}