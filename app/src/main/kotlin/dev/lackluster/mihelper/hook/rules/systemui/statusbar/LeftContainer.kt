package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.isGone
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.makeAccessible
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.notification_icon_area
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_view_state_tag
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconManager.getLeftBlockList
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconManager.leftBlockList
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.factory.dp
import io.github.libxposed.api.XposedInterface

object LeftContainer : StaticHooker() {
    private var Any.leftStatusIconContainer by extraOf<ViewGroup>("KEY_LEFT_STATUS_ICON_CONTAINER")
    private var Any.leftStatusIconManager by extraOf<Any>("KEY_LEFT_STATUS_ICON_MANAGER")
    private var ViewGroup.islandRect by extraOf<Rect>("KEY_ISLAND_RECT")
    private var ViewGroup.islandShowing by extraOf("KEY_ISLAND_SHOWING", false)

    private val leftContainerMode by Preferences.SystemUI.StatusBar.IconTuner.LEFT_CONTAINER.lazyGet()

    private val clzMiuiStatusIconContainer by "com.android.systemui.statusbar.views.MiuiStatusIconContainer".lazyClassOrNull()
    private val ctorMiuiStatusIconContainer by lazy {
        clzMiuiStatusIconContainer?.resolve()?.firstConstructorOrNull {
            parameters(Context::class)
            parameterCount = 1
        }?.toTyped()
    }
    private val leftContainers = mutableListOf<ViewGroup>()

    override fun onInit() {
        updateSelfState(leftContainerMode != 0)
    }

    override fun onHook() {
        val metUpdateLayoutFrom = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "updateLayoutFrom"
        }?.toTyped<Unit>()
        val metSetNeedLimitIcon = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setNeedLimitIcon"
        }?.toTyped<Unit>()
        val metSetIslandController = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setIslandController"
        }?.toTyped<Unit>()
        val metSetIgnoredSlots = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setIgnoredSlots"
        }?.toTyped<Unit>()
        val metSetAnimatable = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setAnimatable"
        }?.toTyped<Unit>()
        val metSetAnimatorController = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setAnimatorController"
        }?.toTyped<Unit>()
        val fldAnimatable = clzMiuiStatusIconContainer?.resolve()?.firstFieldOrNull {
            name = "animatable"
        }?.toTyped<Any>()
        val fldAnimatorController = clzMiuiStatusIconContainer?.resolve()?.firstFieldOrNull {
            name = "animatorController"
        }?.toTyped<Any>()
        val clzStatusBarIconControllerImpl = "com.android.systemui.statusbar.phone.ui.StatusBarIconControllerImpl".toClassOrNull()
        val metAddIconGroup = clzStatusBarIconControllerImpl?.resolve()?.firstMethodOrNull {
            name = "addIconGroup"
            parameterCount = 1
        }?.toTyped<Unit>()
        val fldStatusBarIconList = clzStatusBarIconControllerImpl?.resolve()?.firstFieldOrNull {
            name = "mStatusBarIconList"
        }?.toTyped<Any>()
        val fldSlots = "com.android.systemui.statusbar.phone.ui.StatusBarIconList".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "mSlots"
            }?.toTyped<List<*>>()
        }
        val fldSlotName = $$"com.android.systemui.statusbar.phone.ui.StatusBarIconList$Slot".toClassOrNull()?.let {
            it.resolve().firstFieldOrNull {
                name = "mName"
            }?.toTyped<String>()
        }
        val enumValueOf = "com.android.systemui.statusbar.phone.StatusBarLocation".toClassOrNull()?.let {
            it.resolve().firstMethodOrNull {
                name = "valueOf"
                parameters(String::class)
                modifiers(Modifiers.STATIC)
            }
        }
        val enumStatusBarLocationHome = enumValueOf?.invoke("HOME")
        val enumStatusBarLocationKeyguard = enumValueOf?.invoke("KEYGUARD")
        val enumValueOf2 =
            $$"com.android.systemui.statusbar.anim.MiuiStatusBarIconAnimatorController$StateTransition".toClassOrNull()?.let {
                it.resolve().firstMethodOrNull {
                    name = "valueOf"
                    parameters(String::class)
                    modifiers(Modifiers.STATIC)
                }
            }
        val enumStateTransitionIslandHide = enumValueOf2?.invoke("ISLAND_HIDE")
        val enumStateTransitionIslandShow = enumValueOf2?.invoke("ISLAND_SHOW")
        // 状态栏
        "com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment".toClassOrNull()?.apply {
            val fldStatusBar = resolve().firstFieldOrNull {
                name = "mStatusBar"
                superclass()
            }?.toTyped<FrameLayout>()
            val fldDarkIconManagerFactory = resolve().firstFieldOrNull {
                name = "mDarkIconManagerFactory"
                superclass()
            }?.toTyped<Any>()
            val fldHomeStatusBarComponent = resolve().firstFieldOrNull {
                name = "mHomeStatusBarComponent"
                superclass()
            }?.toTyped<Any>()
            val fldStatusBarIconController = resolve().firstFieldOrNull {
                name = "mStatusBarIconController"
                superclass()
            }?.toTyped<Any>()
            val fldStatusContainer = resolve().firstFieldOrNull {
                name = "mStatusContainer"
                superclass()
            }?.toTyped<Any>()
            val fldNotificationIconAreaInner = resolve().firstFieldOrNull {
                name = "mNotificationIconAreaInner"
                superclass()
            }?.toTyped<Any>()
            val metCancelAnimate = resolve().firstMethodOrNull {
                name = "cancelAnimate"
                parameters(View::class)
                superclass()
            }?.toTyped<Unit>()
            val metAnimateHiddenState = resolve().firstMethodOrNull {
                name = "animateHiddenState"
                parameters(Int::class, View::class, Boolean::class, Boolean::class)
                superclass()
            }?.self?.apply { makeAccessible() }
            val metAnimateShow = resolve().firstMethodOrNull {
                name = "animateShow"
                parameters(View::class, Boolean::class, Boolean::class)
                superclass()
            }?.self?.apply { makeAccessible() }
            resolve().firstMethodOrNull {
                name = "onViewCreated"
            }?.hook {
                val ori = proceed()
                val mStatusBar = fldStatusBar?.get(thisObject) ?: return@hook result(ori)
                val leftStatusIcons = getOrPutStatusIconContainer(mStatusBar, mStatusBar.context, true) ?: return@hook result(ori)
                mStatusBar.findViewById<ViewGroup>(notification_icon_area)?.let { notificationContainer ->
                    val parent = notificationContainer.parent as? ViewGroup
                    parent?.apply {
                        addView(
                            leftStatusIcons,
                            indexOfChild(notificationContainer),
                            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                        )
                    }
                }
                val darkIconDispatcher = fldHomeStatusBarComponent?.get(thisObject)?.let {
                    it.asResolver().firstFieldOrNull {
                        name = "darkIconDispatcher"
                    }?.get()
                }
                val darkIconManager = fldDarkIconManagerFactory?.get(thisObject)?.let {
                    it.asResolver().firstMethodOrNull {
                        name = "create"
                        parameterCount = 3
                    }?.invoke(leftStatusIcons, enumStatusBarLocationHome, darkIconDispatcher)
                }
                val statusBarIconController = fldStatusBarIconController?.get(thisObject) ?: return@hook result(ori)
                metAddIconGroup?.invoke(statusBarIconController, darkIconManager)
                val blockList = fldStatusBarIconList?.get(statusBarIconController)?.let { controller ->
                    fldSlots?.get(controller)?.let { slots ->
                        slots.mapNotNull { slot ->
                            fldSlotName?.get(slot)
                        }
                    }
                }?.let {
                    getLeftBlockList(it)
                } ?: leftBlockList
                metSetIgnoredSlots?.invoke(leftStatusIcons, blockList)
                fldStatusContainer?.get(thisObject)?.let { container ->
                    fldAnimatable?.get(container)?.let {
                        metSetAnimatable?.invoke(leftStatusIcons, it)
                    }
                    fldAnimatorController?.get(container)?.let {
                        metSetAnimatorController?.invoke(leftStatusIcons, it)
                    }
                }
                result(ori)
            }
            metAnimateShow?.hook {
                val ori = proceed()
                val mNotificationIconAreaInner = fldNotificationIconAreaInner?.get(thisObject)
                if (mNotificationIconAreaInner != null && getArg(0) as? View == mNotificationIconAreaInner) {
                    val mStatusBar = fldStatusBar?.get(thisObject)
                    val leftStatusIcons = mStatusBar?.let {
                        getOrPutStatusIconContainer(it, it.context, true)
                    }
                    module.getInvoker(metAnimateShow).setType(XposedInterface.Invoker.Type.ORIGIN).invoke(
                        thisObject,
                        leftStatusIcons,
                        getArg(1),
                        getArg(2),
                    )
                }
                result(ori)
            }
            metAnimateHiddenState?.hook {
                val ori = proceed()
                val mNotificationIconAreaInner = fldNotificationIconAreaInner?.get(thisObject)
                if (mNotificationIconAreaInner != null && getArg(1) as? View == mNotificationIconAreaInner) {
                    val mStatusBar = fldStatusBar?.get(thisObject)
                    val leftStatusIcons = mStatusBar?.let {
                        getOrPutStatusIconContainer(it, it.context, true)
                    }
                    module.getInvoker(metAnimateHiddenState).setType(XposedInterface.Invoker.Type.ORIGIN).invoke(
                        thisObject,
                        getArg(0),
                        leftStatusIcons,
                        getArg(2),
                        getArg(3),
                    )
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "onDestroyView"
            }?.hook {
                val ori = proceed()
                val mStatusBar = fldStatusBar?.get(thisObject)
                val leftStatusIcons = mStatusBar?.let {
                    getOrPutStatusIconContainer(it, it.context, true)
                }
                if (leftStatusIcons != null) {
                    metCancelAnimate?.invoke(thisObject, leftStatusIcons)
                }
                leftContainers.clear()
                result(ori)
            }
        }
        "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "onAttachedToWindow"
            }?.hook {
                val ori = proceed()
                val view = thisObject as? View
                val leftStatusIcons = view?.let {
                    getOrPutStatusIconContainer(it, it.context, true)
                }
                if (leftStatusIcons != null) {
                    metUpdateLayoutFrom?.invoke(leftStatusIcons, 0)
                    metSetNeedLimitIcon?.invoke(leftStatusIcons, true)
                }
                result(ori)
            }
        }
        $$"com.android.systemui.statusbar.StatusBarIslandControllerImpl$IslandStateHandler".toClassOrNull()?.apply {
            val fldIslandRect = resolve().firstFieldOrNull {
                name = "islandRect"
            }?.toTyped<Rect>()
            val fldIslandShowing = resolve().firstFieldOrNull {
                name = "islandShowing"
            }?.toTyped<Boolean>()
            resolve().firstMethodOrNull {
                name = "islandUpdate"
            }?.hook {
                val ori = proceed()
                val islandRect = fldIslandRect?.get(thisObject)
                val islandShowing = fldIslandShowing?.get(thisObject) ?: false
                if (islandRect != null) {
                    leftContainers.forEach { viewGroup ->
                        viewGroup.islandRect = islandRect
                        viewGroup.islandShowing = islandShowing
                        viewGroup.requestLayout()
                    }
                }
                result(ori)
            }
        }
        clzMiuiStatusIconContainer?.apply {
            val fldAnimatorController = resolve().firstFieldOrNull {
                name = "animatorController"
            }?.toTyped<Any>()
            val clzStatusIconDisplayable = "com.android.systemui.statusbar.StatusIconDisplayable".toClassOrNull()
            val metIsIconVisible = clzStatusIconDisplayable?.resolve()?.firstMethodOrNull {
                name = "isIconVisible"
            }?.toTyped<Boolean>()
            val metGetRemoveFlag = clzStatusIconDisplayable?.resolve()?.firstMethodOrNull {
                name = "getRemoveFlag"
            }?.toTyped<Boolean>()
            val metSetVisibleState = clzStatusIconDisplayable?.resolve()?.firstMethodOrNull {
                name = "setVisibleState"
                parameters(Int::class, Boolean::class)
            }?.toTyped<Unit>()
            val clzNewStatusIconState = "com.android.systemui.statusbar.views.NewStatusIconState".toClassOrNull()
            val fldLayoutTranslationX = clzNewStatusIconState?.resolve()?.firstFieldOrNull {
                name = "layoutTranslationX"
            }?.toTyped<Float>()
            val fldVisibleState = clzNewStatusIconState?.resolve()?.firstFieldOrNull {
                name = "visibleState"
            }?.toTyped<Int>()
            val fldInIslandState = clzNewStatusIconState?.resolve()?.firstFieldOrNull {
                name = "inIslandState"
            }?.toTyped<Int>()
            val metAnimateTo = clzNewStatusIconState?.resolve()?.firstMethodOrNull {
                name = "animateTo"
                superclass()
            }?.toTyped<Unit>()
            val metCreateFolmeAnimation = "com.android.systemui.statusbar.anim.MiuiStatusBarIconAnimatorController".toClassOrNull()
                ?.resolve()?.firstMethodOrNull {
                    name = "createFolmeAnimation"
                    parameterCount = 3
                }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "onLayout"
            }?.hook {
                val ori = proceed()
                val container = thisObject as? ViewGroup
                if (container == null || container !in leftContainers) return@hook result(ori)
                val animController = fldAnimatorController?.get(container)
                val islandRect = container.islandRect ?: return@hook result(ori)
                val islandShowing = container.islandShowing ?: false
                val containerLoc = IntArray(2)
                container.getLocationOnScreen(containerLoc)
                val islandPadding = 2.dp(container.context)
                for (i in (container.childCount - 1) downTo 0) {
                    val iconView = container.getChildAt(i)
                    val viewState = iconView.getTag(status_bar_view_state_tag) ?: continue
                    val isVisible = metIsIconVisible?.invoke(iconView) ?: false
                    val removeFlag = metGetRemoveFlag?.invoke(iconView) ?: false

                    if (iconView.isGone || !isVisible || removeFlag) {
                        continue
                    }

                    val layoutTx = fldLayoutTranslationX?.get(viewState) ?: 0.0f
                    val iconLeftAbsolute = containerLoc[0] + layoutTx
                    val iconRightAbsolute = iconLeftAbsolute + iconView.width
                    val isColliding = islandShowing && !islandRect.isEmpty &&
                            iconRightAbsolute > (islandRect.left - islandPadding) &&
                            iconLeftAbsolute < (islandRect.right + islandPadding)

                    if (isColliding) {
                        val currentState = fldVisibleState?.get(viewState)
                        if (currentState != null && currentState != 2) {
                            fldVisibleState.set(viewState, 2) // 2 = 隐藏
                            fldInIslandState?.set(viewState, 10) // 10 = 在岛下方
                            val animProps = metCreateFolmeAnimation?.invoke(
                                animController,
                                enumStateTransitionIslandShow,
                                iconView,
                                viewState
                            )
                            metAnimateTo?.invoke(viewState, iconView, animProps)
                            metSetVisibleState?.invoke(iconView, 2, false)
                        }
                    } else {
                        val currentState = fldVisibleState?.get(viewState)
                        if (currentState == 2) {
                            fldVisibleState.set(viewState, 0) // 0 = 可见
                            fldInIslandState?.set(viewState, 20) // 20 = 正常
                            val animProps = metCreateFolmeAnimation?.invoke(
                                animController,
                                enumStateTransitionIslandHide,
                                iconView,
                                viewState
                            )
                            metAnimateTo?.invoke(viewState, iconView, animProps)
                            metSetVisibleState?.invoke(iconView, 0, false)
                        }
                    }
                }
                result(ori)
            }
        }
        // 锁屏
        if (leftContainerMode != 2) return
        "com.android.systemui.statusbar.phone.KeyguardStatusBarViewController".toClassOrNull()?.apply {
            val fldView = resolve().firstFieldOrNull {
                name = "mView"
                superclass()
            }?.toTyped<ViewGroup>()
            val fldCarrier = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name {
                    it.startsWith("mCarrier")
                }
                type { View::class.java.isAssignableFrom(it) }
            }?.toTyped<View>()
            val fldLightLockScreenWallpaper = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mLightLockScreenWallpaper"
            }?.toTyped<Boolean>()
            val fldDep = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mDep"
            }?.toTyped<Any>()
            val clzKeyguardStatusBarViewControllerInject = "com.android.systemui.statusbar.phone.KeyguardStatusBarViewControllerInject".toClassOrNull()
            val fldIconDispatcher = clzKeyguardStatusBarViewControllerInject?.resolve()?.firstFieldOrNull {
                name = "iconDispatcher"
            }?.toTyped<Any>()
            val fldIslandController = clzKeyguardStatusBarViewControllerInject?.resolve()?.firstFieldOrNull {
                name = "islandController"
            }?.toTyped<Any>()
            val metGetLightModeIconColorSingleTone = "com.android.systemui.plugins.DarkIconDispatcher".toClassOrNull()
                ?.resolve()?.firstMethodOrNull {
                    name = "getLightModeIconColorSingleTone"
                }?.toTyped<Int>()
            val fldIconManagerFactory = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mIconManagerFactory"
            }?.toTyped<Any>()
            val metCreateMiuiIconManager = $$"com.android.systemui.statusbar.phone.ui.TintedIconManager$Factory".toClassOrNull()
                ?.resolve()?.firstMethodOrNull {
                    name = "createMiuiIconManager"
                    parameterCount = 4
                }?.toTyped<Any>()
            val fldIconController = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mIconController"
            }?.toTyped<Any>()
            resolve().firstMethodOrNull {
                name = "onViewAttached"
            }?.hook {
                val ori = proceed()
                val miuiKeyguardStatusBarView = fldView?.get(thisObject) ?: return@hook result(ori)
                val leftStatusIcons = getOrPutStatusIconContainer(miuiKeyguardStatusBarView, miuiKeyguardStatusBarView.context, false) ?: return@hook result(ori)
                fldCarrier?.get(miuiKeyguardStatusBarView)?.let { carrier ->
                    val parent = carrier.parent as? ViewGroup
                    parent?.apply {
                        val position = indexOfChild(carrier)
                        val params = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        removeView(carrier)
                        addView(
                            LinearLayout(context).apply {
                                orientation = LinearLayout.HORIZONTAL
                                layoutDirection = View.LAYOUT_DIRECTION_INHERIT
                                layoutParams = params
                                addView(carrier)
                                addView(leftStatusIcons)
                            },
                            position
                        )
                    }
//                        leftStatusIcons.doOnLayout {
//                            val parentDirection = (it.parent as? ViewGroup)?.layoutDirection ?: View.LAYOUT_DIRECTION_LTR
//                            it.layoutDirection = if (parentDirection == View.LAYOUT_DIRECTION_LTR) {
//                                View.LAYOUT_DIRECTION_RTL
//                            } else {
//                                View.LAYOUT_DIRECTION_LTR
//                            }
//                        }
                }
                val mLightLockScreenWallpaper = fldLightLockScreenWallpaper?.get(miuiKeyguardStatusBarView) ?: false
                val mDep = fldDep?.get(miuiKeyguardStatusBarView)
                val lightModeIconColorSingleTone = fldIconDispatcher?.get(mDep)?.let { iconDispatcher ->
                    metGetLightModeIconColorSingleTone?.invoke(iconDispatcher)
                }
                val createMiuiIconManager = fldIconManagerFactory?.get(miuiKeyguardStatusBarView)?.let { iconFactory ->
                    metCreateMiuiIconManager?.invoke(
                        iconFactory,
                        leftStatusIcons,
                        enumStatusBarLocationKeyguard,
                        mLightLockScreenWallpaper,
                        lightModeIconColorSingleTone
                    )
                }
                miuiKeyguardStatusBarView.leftStatusIconManager = createMiuiIconManager
                val statusBarIconController = fldIconController?.get(miuiKeyguardStatusBarView) ?: return@hook result(ori)
                metAddIconGroup?.invoke(statusBarIconController, createMiuiIconManager)
                val blockList = fldStatusBarIconList?.get(statusBarIconController)?.let { controller ->
                    fldSlots?.get(controller)?.let { slots ->
                        slots.mapNotNull { slot ->
                            fldSlotName?.get(slot)
                        }
                    }
                }?.let {
                    getLeftBlockList(it)
                } ?: leftBlockList
                metSetIgnoredSlots?.invoke(leftStatusIcons, blockList)
                metUpdateLayoutFrom?.invoke(leftStatusIcons, 1)
                val islandController = mDep?.let { fldIslandController?.get(it) }
                metSetIslandController?.invoke(
                    leftStatusIcons,
                    islandController,
                    1
                )
                result(ori)
            }
        }
        clzMiuiKeyguardStatusBarView?.apply {
            val fldInit = resolve().optional(true).firstFieldOrNull {
                name = "mInit"
            }?.toTyped<Boolean>()
            val fldShowCarrier = resolve().optional(true).firstFieldOrNull {
                name = "mShowCarrier"
            }?.toTyped<Boolean>()
            val fldStatusIconContainer = resolve().optional(true).firstFieldOrNull {
                name = "mStatusIconContainer"
                superclass()
            }?.toTyped<Any>()
            var hookInit = false
            resolve().firstMethodOrNull {
                name = "initCallback"
            }?.hook {
                val ori = proceed()
                val view = thisObject as? View
                if (!hookInit && view != null && fldInit?.get(thisObject) == true) {
                    val leftStatusIconContainer = getOrPutStatusIconContainer(view, view.context, false) ?: return@hook result(ori)
                    metSetNeedLimitIcon?.invoke(leftStatusIconContainer, true)
                    fldStatusIconContainer?.get(thisObject)?.let { container ->
                        fldAnimatable?.get(container)?.let {
                            metSetAnimatable?.invoke(leftStatusIconContainer, it)
                        }
                        fldAnimatorController?.get(container)?.let {
                            metSetAnimatorController?.invoke(leftStatusIconContainer, it)
                        }
                    }
                    hookInit = true
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name {
                    it.startsWith("updateCarrierVisibility")
                }
            }?.hook {
                val ori = proceed()
                val view = thisObject as? View
                if (view != null) {
                    val leftStatusIconContainer = getOrPutStatusIconContainer(view, view.context, false) ?: return@hook result(ori)
                    leftStatusIconContainer.visibility =
                        if (fldShowCarrier?.get(thisObject) == true) View.VISIBLE
                        else View.GONE
                }
                result(ori)
            }
            val fldTintedIconManager = resolve().firstFieldOrNull {
                name = "mTintedIconManager"
                superclass()
            }?.toTyped<Any>()
            val clzMiuiLightDarkIconManager = "com.android.systemui.statusbar.phone.MiuiLightDarkIconManager".toClassOrNull()
            val metSetLight = clzMiuiLightDarkIconManager?.resolve()?.firstMethodOrNull {
                name = "setLight"
                parameters(Int::class, Boolean::class, Boolean::class)
            }?.toTyped<Unit>()
            val fldColor = clzMiuiLightDarkIconManager?.resolve()?.firstFieldOrNull {
                name = "mColor"
                type(Int::class)
            }?.toTyped<Int>()
            val fldLight = clzMiuiLightDarkIconManager?.resolve()?.firstFieldOrNull {
                name = "mLight"
                type(Boolean::class)
            }?.toTyped<Boolean>()
            resolve().firstMethodOrNull {
                name = "updateIconsAndTextColors"
            }?.hook {
                val ori = proceed()
                val view = thisObject as? View
                val leftStatusIconManager = view?.leftStatusIconManager
                val mTintedIconManager = fldTintedIconManager?.get(thisObject)
                val mColor = mTintedIconManager?.let { fldColor?.get(it) }
                val mLight = mTintedIconManager?.let { fldLight?.get(it) }
                if (leftStatusIconManager != null && mColor != null && mLight != null) {
                    metSetLight?.invoke(leftStatusIconManager, mColor, mLight, false)
                }
                result(ori)
            }
        }
    }

    private fun getOrPutStatusIconContainer(obj: Any, context: Context, remember: Boolean): ViewGroup? {
        obj.leftStatusIconContainer?.let {
            return it
        }
        val container = ctorMiuiStatusIconContainer?.newInstance(context) as? LinearLayout
        if (container != null) {
            obj.leftStatusIconContainer = container
            if (remember) leftContainers.add(container)
        }
        return container
    }
}