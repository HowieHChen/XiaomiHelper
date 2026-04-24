package dev.lackluster.mihelper.hook.rules.systemui.mobile

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_icon_height
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzStatusBarIconControllerImpl
import dev.lackluster.mihelper.hook.rules.systemui.compat.FlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.FlowCompat.collectFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.IconControllerCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.MutableStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.ReadonlyStateFlowCompat
import dev.lackluster.mihelper.hook.rules.systemui.compat.TripleCompat
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.hook.utils.HostExecutor
import dev.lackluster.mihelper.utils.SystemProperties
import java.io.FileInputStream
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

object StackedMobileIcon : StaticHooker() {
    private val enabled by Preferences.SystemUI.StatusBar.StackedMobile.ENABLED.lazyGet()
    private val prefFontPath by Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_ORIGINAL.lazyGet()
    private val defFontPath by lazy {
        SystemProperties.get("ro.miui.ui.font.mi_font_path", VARIABLE_FONT_DEFAULT_PATH)
    }

    private val simCacheMap = HashMap<Int, SimPipelineCache>()
    private val flowJobs = mutableListOf<Any?>()

    private var relaySim1ConnectionInfo: Any? = null
    private var relaySim2ConnectionInfo: Any? = null

    private val clzMiuiMobileIconVMImpl by "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MiuiMobileIconVMImpl".lazyClassOrNull()
    private val fldShowName by lazy {
        clzMiuiMobileIconVMImpl?.resolve()?.firstFieldOrNull {
            name = "showName"
        }?.toTyped<Any>()
    }
    private val fldIconInteractor by lazy {
        clzMiuiMobileIconVMImpl?.resolve()?.firstFieldOrNull {
            name = "iconInteractor"
        }?.toTyped<Any>()
    }
    private val fldOriginIconInteractor by lazy {
        clzMiuiMobileIconVMImpl?.resolve()?.firstFieldOrNull {
            name = "originIconInteractor"
        }?.toTyped<Any>()
    }

    private val clzMobileIconInteractor by "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconInteractor".lazyClassOrNull()
    private val metGetSignalLevelIcon by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "getSignalLevelIcon"
        }?.toTyped<Any>()
    }
    private val metIsDataConnected by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "isDataConnected"
        }?.toTyped<Any>()
    }
    private val metIsInService by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "isInService"
        }?.toTyped<Any>()
    }
    private val metIsRoaming by lazy {
        clzMobileIconInteractor?.resolve()?.firstMethodOrNull {
            name = "isRoaming"
        }?.toTyped<Any>()
    }

    private val fldIsAirplaneMode by lazy {
        "com.android.systemui.statusbar.pipeline.airplane.domain.interactor.AirplaneModeInteractor".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "isAirplaneMode"
            }?.toTyped<Any>()
        }
    }

    private val fldWifiAvailable by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MiuiMobileIconInteractorImpl".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "wifiAvailable"
            }?.toTyped<Any>()
        }
    }

    private val fldSubscriptionId by lazy {
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconViewModel".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "subscriptionId"
            }?.toTyped<Any>()
        }
    }

    private val clzSignalIconModelCellular by $$"com.android.systemui.statusbar.pipeline.mobile.domain.model.SignalIconModel$Cellular".lazyClassOrNull()
    private val fldLevel by lazy {
        clzSignalIconModelCellular?.resolve()?.firstFieldOrNull {
            name = "level"
        }?.toTyped<Int>()
    }
    private val fldNumberOfLevels by lazy {
        clzSignalIconModelCellular?.resolve()?.firstFieldOrNull {
            name = "numberOfLevels"
        }?.toTyped<Int>()
    }

    private val fldContext by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstFieldOrNull {
            name = "mContext"
        }?.toTyped<Context>()
    }
    private val fldStatusBarIconList by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstFieldOrNull {
            name = "mStatusBarIconList"
        }?.toTyped<Any>()
    }
    private val metHandleSet by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstMethodOrNull {
            name = "handleSet"
        }?.toTyped<Unit>()
    }

    private val metGetIconHolder by lazy {
        "com.android.systemui.statusbar.phone.ui.StatusBarIconList".toClassOrNull()?.let {
            it.resolve().firstMethodOrNull {
                name = "getIconHolder"
                parameters(Int::class, String::class)
            }?.toTyped<Any>()
        }
    }

    private val fldIcon by lazy {
        "com.android.systemui.statusbar.phone.StatusBarIconHolder".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "icon"
            }?.toTyped<Any>()
        }
    }

    private val clzStatusBarIcon by "com.android.internal.statusbar.StatusBarIcon".lazyClassOrNull()
    private val fldRealIcon by lazy {
        clzStatusBarIcon?.resolve()?.firstFieldOrNull {
            name = "icon"
        }?.toTyped<Icon>()
    }
    private val fldPkg by lazy {
        clzStatusBarIcon?.resolve()?.firstFieldOrNull {
            name = "pkg"
        }?.toTyped<String>()
    }

    override fun onInit() {
        updateSelfState(enabled)
    }

    override fun onHook() {
        // 强制着色来自动反色
        "com.android.systemui.statusbar.StatusBarIconView".toClassOrNull()?.apply {
            val fldSlot = resolve().firstFieldOrNull {
                name = "mSlot"
            }?.toTyped<String>()
            val metSetDecorColor = resolve().firstMethodOrNull {
                name = "setDecorColor"
                parameters(Int::class)
            }?.toTyped<Unit>()
            val metGetTint = "com.android.systemui.statusbar.DarkIconDispatcherExt".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "getTint"
                parameterCount = 3
                modifiers(Modifiers.STATIC)
            }?.toTyped<Int>()
            resolve().firstMethodOrNull {
                name = "updateLightDarkTint"
            }?.hook {
                val ori = proceed()
                val iconView = thisObject as? ImageView
                val slot = fldSlot?.get(thisObject)
                if (iconView == null || slot == null) return@hook result(ori)
                when (slot) {
                    Constants.IconSlots.STACKED_MOBILE_TYPE, Constants.IconSlots.STACKED_MOBILE_ICON,
                    Constants.IconSlots.SINGLE_MOBILE_SIM1, Constants.IconSlots.SINGLE_MOBILE_SIM2,
                        -> {
                        metGetTint?.invoke(
                            null,
                            getArg(0),
                            iconView,
                            getArg(2),
                        )?.let { tint ->
//                            iconView.imageTintList = ColorStateList.valueOf(tint)
                            iconView.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            metSetDecorColor?.invoke(iconView, tint)
                        }
                    }
                }
                result(ori)
            }
        }
        // 刷新图标的 Flow
        "com.android.systemui.statusbar.pipeline.mobile.ui.MobileUiAdapter".toClassOrNull()?.apply {
            val fldIconController = resolve().firstFieldOrNull {
                name = "iconController"
            }?.toTyped<Any>()
            val fldScope = resolve().firstFieldOrNull {
                name = "scope"
            }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "start"
            }?.hook {
                val ori = proceed()
                val iconController = fldIconController?.get(thisObject)
                val coroutineScope = fldScope?.get(thisObject)
                val context = fldContext?.get(iconController)
                if (iconController == null || coroutineScope == null || context == null) {
                    return@hook result(ori)
                }
                // 刷新图标通用方法
                val renderIcon = { slot: String, state: CellularIconState ->
                    val icon = CellularIconRenderEngine.getIcon(context, state)
                    if (icon != null) {
                        updateMobileIcon(iconController, slot, icon)
                        IconControllerCompat.setIconVisibility(iconController, slot, true)
                    } else {
                        IconControllerCompat.setIconVisibility(iconController, slot, false)
                    }
                }
                // 初始化图标缓存
                HostExecutor.execute(
                    tag = "PRELOAD_STACKED_MOBILE_SVG",
                    backgroundTask = {
                        val customSingleSvg = runCatching {
                            module.openRemoteFile(Constants.REMOTE_FILE_STACKED_SIGNAL_SINGLE).use { pfd ->
                                FileInputStream(pfd.fileDescriptor).bufferedReader().use { it.readText() }
                            }
                        }.getOrNull()

                        val customStackedSvg = runCatching {
                            module.openRemoteFile(Constants.REMOTE_FILE_STACKED_SIGNAL_STACKED).use { pfd ->
                                FileInputStream(pfd.fileDescriptor).bufferedReader().use { it.readText() }
                            }
                        }.getOrNull()

                        if (prefFontPath.isNotBlank() && prefFontPath != defFontPath) {
                            runCatching {
                                module.openRemoteFile(Constants.REMOTE_FILE_STACKED_MOBILE_TYPE_FONT).use { pfd ->
                                    CellularIconRenderEngine.preload(
                                        context = context.applicationContext,
                                        iconHeightResId = status_bar_icon_height,
                                        remoteFontFd = pfd,
                                        customSingleSvg = customSingleSvg,
                                        customStackedSvg = customStackedSvg,
                                    )
                                }
                            }.onFailure {
                                e(it) { "Failed to read remote font file, fallback to MiSansVF.ttf" }
                                CellularIconRenderEngine.preload(
                                    context = context.applicationContext,
                                    iconHeightResId = status_bar_icon_height,
                                    remoteFontFd = null,
                                    customSingleSvg = customSingleSvg,
                                    customStackedSvg = customStackedSvg,
                                )
                            }
                        } else {
                            CellularIconRenderEngine.preload(
                                context = context.applicationContext,
                                iconHeightResId = status_bar_icon_height,
                                remoteFontFd = null,
                                customSingleSvg = customSingleSvg,
                                customStackedSvg = customStackedSvg,
                            )
                        }
                    },
                    runOnMain = true,
                    onResult = {
                        // 数据在图标就绪前到达，补一次刷新
                        CellularIconInteractor.proxyStackedSignal.getValue()?.let { it1 ->
                            renderIcon(Constants.IconSlots.STACKED_MOBILE_ICON, it1)
                        }
                        CellularIconInteractor.proxyStandaloneNetType.getValue()?.let { it1 ->
                            renderIcon(Constants.IconSlots.STACKED_MOBILE_TYPE, it1)
                        }
                        CellularIconInteractor.proxySim1Signal.getValue()?.let { it1 ->
                            renderIcon(Constants.IconSlots.SINGLE_MOBILE_SIM1, it1)
                        }
                        CellularIconInteractor.proxySim2Signal.getValue()?.let { it1 ->
                            renderIcon(Constants.IconSlots.SINGLE_MOBILE_SIM2, it1)
                        }

                    }
                )
                // 启动刷新图标的 Flow
                CellularIconInteractor.proxyStackedSignal.collectFlow(coroutineScope) {
                    renderIcon(Constants.IconSlots.STACKED_MOBILE_ICON, it)
                }.let { flowJobs.add(it) }
                CellularIconInteractor.proxyStandaloneNetType.collectFlow(coroutineScope) {
                    renderIcon(Constants.IconSlots.STACKED_MOBILE_TYPE, it)
                }.let { flowJobs.add(it) }
                CellularIconInteractor.proxySim1Signal.collectFlow(coroutineScope) {
                    renderIcon(Constants.IconSlots.SINGLE_MOBILE_SIM1, it)
                }.let { flowJobs.add(it) }
                CellularIconInteractor.proxySim2Signal.collectFlow(coroutineScope) {
                    renderIcon(Constants.IconSlots.SINGLE_MOBILE_SIM2, it)
                }.let { flowJobs.add(it) }
                result(ori)
            }
        }
        // 监听数据的 Flow
        "com.android.systemui.statusbar.pipeline.mobile.ui.viewmodel.MobileIconsViewModel".toClassOrNull()?.apply {
            val fldAirplaneModeInteractor = resolve().firstFieldOrNull {
                name = "airplaneModeInteractor"
            }?.toTyped<Any>()
            val fldInteractor = resolve().firstFieldOrNull {
                name = "interactor"
            }?.toTyped<Any>()
            val fldMobileSubViewModels = resolve().firstFieldOrNull {
                name = "mobileSubViewModels"
            }?.toTyped<Any>()
            val fldReuseCache = resolve().firstFieldOrNull {
                name = "reuseCache"
            }?.toTyped<ConcurrentHashMap<Int, Any>>()
            val metGetDefaultDataSubId = "com.android.systemui.statusbar.pipeline.mobile.domain.interactor.MobileIconsInteractor".toClassOrNull()?.let {
                it.resolve().firstMethodOrNull {
                    name { it1 ->
                        it1.startsWith("getDefaultDataSubId")
                    }
                }?.toTyped<Any>()
            }
            resolve().firstConstructor().hook {
                val ori = proceed()
                val coroutineScope = args.firstOrNull { CommonClassUtils.clzCoroutineScope?.isInstance(it) == true }
                val reuseCache = fldReuseCache?.get(thisObject)
                val mobileSubViewModels = fldMobileSubViewModels?.get(thisObject)?.let {
                    ReadonlyStateFlowCompat<List<Any?>>().of(it)
                }
                val isAirplaneMode = fldAirplaneModeInteractor?.get(thisObject)?.let {
                    fldIsAirplaneMode?.get(it)?.let { it1 ->
                        ReadonlyStateFlowCompat<Boolean>().of(it1)
                    }
                }
                val defaultDataSubId = fldInteractor?.get(thisObject)?.let {
                    metGetDefaultDataSubId?.invoke(it)?.let { it1 ->
                        ReadonlyStateFlowCompat<Int>().of(it1)
                    }
                }
                if (
                    coroutineScope == null || reuseCache == null || mobileSubViewModels == null ||
                    isAirplaneMode == null || defaultDataSubId == null
                ) {
                    return@hook result(ori)
                }
                // 初始化 CellularIconInteractor
                CellularIconInteractor.start(coroutineScope)
                // CellularIconInteractor 内的全局变量
                isAirplaneMode.collectFlow(coroutineScope) {
                    CellularIconInteractor.isAirplaneMode.setValue(it)
                }.let { flowJobs.add(it) }
                defaultDataSubId.collectFlow(coroutineScope) {
                    CellularIconInteractor.defaultDataSubId.setValue(it)
                }.let { flowJobs.add(it) }
                // 双卡数据合并规则
                mobileSubViewModels.collectFlow(coroutineScope) { vms ->
                    val subIds = vms.mapNotNull {
                        it?.let { it1 -> fldSubscriptionId?.get(it1) } as? Int
                    }
                    d { "subscriptionIdsFlow ${subIds.joinToString(",")} reuseCache ${reuseCache.keys.joinToString(", ")}" }
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
                            simCacheMap[subId] = SimPipelineCache(subId, coroutineScope, showName, miuiInteractor, originInteractor)
                        }
                    }
                    // 3. 动态路由：把缓存池里的数据“接线”到固定的 Proxy 流
                    val subId1 = subIds.getOrNull(0)
                    val subId2 = subIds.getOrNull(1)

                    FlowCompat.cancelJob(relaySim1ConnectionInfo)
                    if (subId1 != null && simCacheMap[subId1] != null) {
                        val cache1 = simCacheMap[subId1]!!
                        cache1.simConnectionInfo.collectFlow(coroutineScope) {
                            CellularIconInteractor.sim1ConnectionInfo.setValue(it)
                        }.let { relaySim1ConnectionInfo = it }
                    } else {
                        CellularIconInteractor.sim1ConnectionInfo.setValue(defSimConnectionInfo)
                    }
                    FlowCompat.cancelJob(relaySim2ConnectionInfo)
                    if (subId2 != null && simCacheMap[subId2] != null) {
                        val cache2 = simCacheMap[subId2]!!
                        cache2.simConnectionInfo.collectFlow(coroutineScope) {
                            CellularIconInteractor.sim2ConnectionInfo.setValue(it)
                        }?.let { relaySim2ConnectionInfo = it }
                    } else {
                        CellularIconInteractor.sim2ConnectionInfo.setValue(defSimConnectionInfo)
                    }
                }.let {
                    flowJobs.add(it)
                }
                result(ori)
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
            e(e) { "Failed to update optimized icon for slot: $slot" }
        }
    }

    class SimPipelineCache(
        val subId: Int,
        coroutineScope: Any,
        mobileTypeNameFlow: Any,
        miuiInteractor: Any,
        originInteractor: Any,
    ) {
        val simConnectionInfo = MutableStateFlowCompat(defSimConnectionInfo)

        private val jobs = mutableListOf<Any?>()

        init {
            // CellularIconInteractor 内的全局变量
            val wifiAvailable = fldWifiAvailable?.get(miuiInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            wifiAvailable?.collectFlow(coroutineScope) {
                CellularIconInteractor.isWifiAvailable.setValue(it)
            }?.let { jobs.add(it) }
            // 组装 SimConnectionInfo
            val mobileTypeName = mobileTypeNameFlow.let { ReadonlyStateFlowCompat<String>().of(it) }
            val signalLevelIcon = metGetSignalLevelIcon?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Any?>().of(it) }
            val isDataConnected = metIsDataConnected?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val isInService = metIsInService?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            val isRoaming = metIsRoaming?.invoke(originInteractor)?.let { ReadonlyStateFlowCompat<Boolean>().of(it) }
            if (signalLevelIcon != null && isDataConnected != null && isInService != null && isRoaming != null) {
                FlowCompat.combineFlows(
                    scope = coroutineScope,
                    src1 = mobileTypeName,  defValue1 = "",
                    src2 = signalLevelIcon, defValue2 = null,
                    src3 = isDataConnected, defValue3 = false,
                    src4 = isInService,     defValue4 = false,
                    src5 = isRoaming,       defValue5 = false,
                    dst = simConnectionInfo
                ) { typeName, signalIconModel, connected, inService, roaming ->
                    val signalLevel: SignalLevel
                    if (signalIconModel == null || clzSignalIconModelCellular?.isInstance(signalIconModel) != true) {
                        signalLevel = SignalLevel.NO_SERVICE
                    } else if (!inService) {
                        signalLevel = SignalLevel.NO_SERVICE
                    } else {
                        val levelNow = fldLevel?.get(signalIconModel)
                        val levelAll = fldNumberOfLevels?.get(signalIconModel)
                        signalLevel = if (levelNow == null || levelAll == null) {
                            SignalLevel.NO_SERVICE
                        } else if (levelNow <= 0 || levelAll <= 0) {
                            SignalLevel(0)
                        } else if (levelNow >= levelAll) {
                            SignalLevel(4)
                        } else {
                            SignalLevel(
                                ((levelNow * SignalLevel.MAX_LEVEL.value) / levelAll.toFloat()).roundToInt()
                            )
                        }
                    }
                    SimConnectionInfo(
                        subId = subId,
                        signalLevel = signalLevel,
                        networkType = typeName,
                        isRoaming = roaming,
                        isDataConnected = connected
                    )
                }.let { jobs.addAll(it) }
            }
        }

        fun destroy() {
            jobs.forEach { job ->
                FlowCompat.cancelJob(job)
            }
            jobs.clear()
        }
    }
}