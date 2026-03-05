package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.notification_icon_area
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_view_state_tag
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiKeyguardStatusBarView
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.IconManager.leftBlockList
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField


object LeftContainer : YukiBaseHooker() {
    private const val KEY_LEFT_STATUS_ICON_CONTAINER = "KEY_LEFT_STATUS_ICON_CONTAINER"
    private const val KEY_LEFT_STATUS_ICON_MANAGER = "KEY_LEFT_STATUS_ICON_MANAGER"
    private const val KEY_ISLAND_RECT = "KEY_ISLAND_RECT"
    private const val KEY_ISLAND_SHOWING = "KEY_ISLAND_SHOWING"

    private val leftContainerMode = Prefs.getInt(IconTuner.LEFT_CONTAINER, 0)

    private val clzMiuiStatusIconContainer by lazy {
        "com.android.systemui.statusbar.views.MiuiStatusIconContainer".toClassOrNull()
    }
    private val ctorMiuiStatusIconContainer by lazy {
        clzMiuiStatusIconContainer?.resolve()?.firstConstructorOrNull {
            parameters(Context::class)
            parameterCount = 1
        }
    }
    private val leftContainers = mutableListOf<ViewGroup>()

    override fun onHook() {
        if (leftContainerMode == 0) return
        val metUpdateLayoutFrom = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "updateLayoutFrom"
        }?.self?.apply { makeAccessible() }
        val metSetNeedLimitIcon = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setNeedLimitIcon"
        }?.self?.apply { makeAccessible() }
        val metSetIslandController = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setIslandController"
        }?.self?.apply { makeAccessible() }
        val metSetIgnoredSlots = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setIgnoredSlots"
        }?.self?.apply { makeAccessible() }
        val metSetAnimatable = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setAnimatable"
        }?.self?.apply { makeAccessible() }
        val metSetAnimatorController = clzMiuiStatusIconContainer?.resolve()?.firstMethodOrNull {
            name = "setAnimatorController"
        }?.self?.apply { makeAccessible() }
        val fldAnimatable = clzMiuiStatusIconContainer?.resolve()?.firstFieldOrNull {
            name = "animatable"
        }?.self?.apply { makeAccessible() }
        val fldAnimatorController = clzMiuiStatusIconContainer?.resolve()?.firstFieldOrNull {
            name = "animatorController"
        }?.self?.apply { makeAccessible() }
        val clzStatusBarIconControllerImpl = "com.android.systemui.statusbar.phone.ui.StatusBarIconControllerImpl".toClassOrNull()
        val metAddIconGroup = clzStatusBarIconControllerImpl?.resolve()?.firstMethodOrNull {
            name = "addIconGroup"
            parameterCount = 1
        }?.self?.apply { makeAccessible() }
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
            }?.self?.apply { makeAccessible() }
            val fldDarkIconManagerFactory = resolve().firstFieldOrNull {
                name = "mDarkIconManagerFactory"
                superclass()
            }?.self?.apply { makeAccessible() }
            val fldHomeStatusBarComponent = resolve().firstFieldOrNull {
                name = "mHomeStatusBarComponent"
                superclass()
            }?.self?.apply { makeAccessible() }
            val fldStatusBarIconController = resolve().firstFieldOrNull {
                name = "mStatusBarIconController"
                superclass()
            }?.self?.apply { makeAccessible() }
            val fldStatusContainer = resolve().firstFieldOrNull {
                name = "mStatusContainer"
                superclass()
            }?.self?.apply { makeAccessible() }
            val fldNotificationIconAreaInner = resolve().firstFieldOrNull {
                name = "mNotificationIconAreaInner"
                superclass()
            }?.self?.apply { makeAccessible() }
            val metCancelAnimate = resolve().firstMethodOrNull {
                name = "cancelAnimate"
                parameters(View::class)
                superclass()
            }?.self?.apply { makeAccessible() }
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
                after {
                    val mStatusBar = fldStatusBar?.get(this.instance) as? FrameLayout ?: return@after
                    val leftStatusIcons = getOrPutAdditionalInstanceField(mStatusBar, mStatusBar.context, true) ?: return@after
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
                    val darkIconDispatcher = fldHomeStatusBarComponent?.get(this.instance)?.let {
                        XposedHelpers.getObjectField(it, "darkIconDispatcher")
                    }
                    val darkIconManager = fldDarkIconManagerFactory?.get(this.instance)?.let {
                        XposedHelpers.callMethod(it, "create", leftStatusIcons, enumStatusBarLocationHome, darkIconDispatcher)
                    }
                    fldStatusBarIconController?.get(this.instance)?.let {
                        metAddIconGroup?.invoke(it, darkIconManager)
                    }
                    metSetIgnoredSlots?.invoke(leftStatusIcons, leftBlockList)
                    fldStatusContainer?.get(this.instance)?.let { container ->
                        fldAnimatable?.get(container)?.let {
                            metSetAnimatable?.invoke(leftStatusIcons, it)
                        }
                        fldAnimatorController?.get(container)?.let {
                            metSetAnimatorController?.invoke(leftStatusIcons, it)
                        }
                    }
                }
            }
            metAnimateShow?.hook {
                after {
                    val mNotificationIconAreaInner = fldNotificationIconAreaInner?.get(this.instance) ?: return@after
                    if (this.args(0).cast<View>() == mNotificationIconAreaInner) {
                        val mStatusBar = fldStatusBar?.get(this.instance) as? FrameLayout ?: return@after
                        val leftStatusIcons = getOrPutAdditionalInstanceField(mStatusBar, mStatusBar.context, true) ?: return@after
                        XposedBridge.invokeOriginalMethod(
                            metAnimateShow,
                            this.instance,
                            arrayOf(
                                leftStatusIcons,
                                this.args(1).boolean(),
                                this.args(2).boolean(),
                            )
                        )
                    }
                }
            }
            metAnimateHiddenState?.hook {
                after {
                    val mNotificationIconAreaInner = fldNotificationIconAreaInner?.get(this.instance) ?: return@after
                    if (this.args(1).cast<View>() == mNotificationIconAreaInner) {
                        val mStatusBar = fldStatusBar?.get(this.instance) as? FrameLayout ?: return@after
                        val leftStatusIcons = getOrPutAdditionalInstanceField(mStatusBar, mStatusBar.context, true) ?: return@after
                        XposedBridge.invokeOriginalMethod(
                            metAnimateHiddenState,
                            this.instance,
                            arrayOf(
                                this.args(0).int(),
                                leftStatusIcons,
                                this.args(2).boolean(),
                                this.args(3).boolean(),
                            )
                        )
                    }
                }
            }
            resolve().firstMethodOrNull {
                name = "onDestroyView"
            }?.hook {
                after {
                    val mStatusBar = fldStatusBar?.get(this.instance) as? FrameLayout ?: return@after
                    val leftStatusIcons = getOrPutAdditionalInstanceField(mStatusBar, mStatusBar.context, true) ?: return@after
                    metCancelAnimate?.invoke(this.instance, leftStatusIcons)
                    leftContainers.clear()
                }
            }
        }
        "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "onAttachedToWindow"
            }?.hook {
                after {
                    val view = this.instance<View>()
                    val leftStatusIconContainer = getOrPutAdditionalInstanceField(view, view.context, true) ?: return@after
                    metUpdateLayoutFrom?.invoke(leftStatusIconContainer, 0)
                    metSetNeedLimitIcon?.invoke(leftStatusIconContainer, true)
                }
            }
        }
        $$"com.android.systemui.statusbar.StatusBarIslandControllerImpl$IslandStateHandler".toClassOrNull()?.apply {
            val fldIslandRect = resolve().firstFieldOrNull {
                name = "islandRect"
            }?.self?.apply { makeAccessible() }
            val fldIslandShowing = resolve().firstFieldOrNull {
                name = "islandShowing"
            }?.self?.apply { makeAccessible() }
            resolve().firstMethodOrNull {
                name = "islandUpdate"
            }?.hook {
                after {
                    val islandRect = fldIslandRect?.get(this.instance) as? Rect ?: return@after
                    val islandShowing = fldIslandShowing?.getBoolean(this.instance) == true
                    leftContainers.forEach { viewGroup ->
                        viewGroup.setAdditionalInstanceField(KEY_ISLAND_RECT, islandRect)
                        viewGroup.setAdditionalInstanceField(KEY_ISLAND_SHOWING, islandShowing)
                        viewGroup.requestLayout()
                    }
                }
            }
        }
        clzMiuiStatusIconContainer?.apply {
            resolve().firstMethodOrNull {
                name = "onLayout"
            }?.hook {
                after {
                    if (this.instance !in leftContainers) return@after
                    val container = this.instance<ViewGroup>()
                    val animController = XposedHelpers.getObjectField(container, "animatorController") ?: return@after
                    val islandRect = container.getAdditionalInstanceField<Rect>(KEY_ISLAND_RECT) ?: return@after
                    val islandShowing = container.getAdditionalInstanceField<Boolean>(KEY_ISLAND_SHOWING) == true
                    val containerLoc = IntArray(2)
                    container.getLocationOnScreen(containerLoc)
                    val islandPadding = 2.dp(container.context)

                    for (i in (container.childCount - 1) downTo 0) {
                        val iconView = container.getChildAt(i)
                        val viewState = iconView.getTag(status_bar_view_state_tag) ?: continue
                        val isVisible = XposedHelpers.callMethod(iconView, "isIconVisible") as? Boolean == true
                        val removeFlag = XposedHelpers.callMethod(iconView, "getRemoveFlag") as? Boolean == true

                        if (iconView.isGone || !isVisible || removeFlag) {
                            continue
                        }

                        val layoutTx = XposedHelpers.getFloatField(viewState, "layoutTranslationX")
                        val iconLeftAbsolute = containerLoc[0] + layoutTx
                        val iconRightAbsolute = iconLeftAbsolute + iconView.width
                        val isColliding = islandShowing && !islandRect.isEmpty &&
                                iconRightAbsolute > (islandRect.left - islandPadding) &&
                                iconLeftAbsolute < (islandRect.right + islandPadding)

                        if (isColliding) {
                            val currentState = XposedHelpers.getIntField(viewState, "visibleState")
                            if (currentState != 2) {
                                XposedHelpers.setIntField(viewState, "visibleState", 2) // 2 = 隐藏
                                XposedHelpers.setIntField(viewState, "inIslandState", 10) // 10 = 在岛下方
                                val animProps = XposedHelpers.callMethod(
                                    animController,
                                    "createFolmeAnimation",
                                    enumStateTransitionIslandShow,
                                    iconView,
                                    viewState
                                )
                                XposedHelpers.callMethod(viewState, "animateTo", iconView, animProps)
                                XposedHelpers.callMethod(iconView, "setVisibleState", 2, false)
                            }
                        } else {
                            val currentState = XposedHelpers.getIntField(viewState, "visibleState")
                            if (currentState == 2) {
                                XposedHelpers.setIntField(viewState, "visibleState", 0) // 0 = 可见
                                XposedHelpers.setIntField(viewState, "inIslandState", 20) // 20 = 正常
                                val animProps = XposedHelpers.callMethod(
                                    animController,
                                    "createFolmeAnimation",
                                    enumStateTransitionIslandHide,
                                    iconView,
                                    viewState
                                )
                                XposedHelpers.callMethod(viewState, "animateTo", iconView, animProps)
                                XposedHelpers.callMethod(iconView, "setVisibleState", 0, false)
                            }
                        }
                    }
                }
            }
        }
        // 锁屏
        if (leftContainerMode != 2) return
        "com.android.systemui.statusbar.phone.KeyguardStatusBarViewController".toClassOrNull()?.apply {
            val fldView = resolve().firstFieldOrNull {
                name = "mView"
                superclass()
            }?.self?.apply { makeAccessible() }
            val fldCarrier = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name {
                    it.startsWith("mCarrier")
                }
                type { View::class.java.isAssignableFrom(it) }
            }?.self?.apply { makeAccessible() }
            val fldLightLockScreenWallpaper = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mLightLockScreenWallpaper"
            }?.self?.apply { makeAccessible() }
            val fldDep = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mDep"
            }?.self?.apply { makeAccessible() }
            val fldIconManagerFactory = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mIconManagerFactory"
            }?.self?.apply { makeAccessible() }
            val fldIconController = clzMiuiKeyguardStatusBarView?.resolve()?.firstFieldOrNull {
                name = "mIconController"
            }?.self?.apply { makeAccessible() }
            resolve().firstMethodOrNull {
                name = "onViewAttached"
            }?.hook {
                after {
                    val miuiKeyguardStatusBarView = fldView?.get(this.instance) as? ViewGroup ?: return@after
                    val leftStatusIcons = getOrPutAdditionalInstanceField(miuiKeyguardStatusBarView, miuiKeyguardStatusBarView.context, false) ?: return@after
                    (fldCarrier?.get(miuiKeyguardStatusBarView) as? View)?.let { carrier ->
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
                        leftStatusIcons.doOnLayout {
                            val parentDirection = (it.parent as? ViewGroup)?.layoutDirection ?: View.LAYOUT_DIRECTION_LTR
                            it.layoutDirection = if (parentDirection == View.LAYOUT_DIRECTION_LTR) {
                                View.LAYOUT_DIRECTION_RTL
                            } else {
                                View.LAYOUT_DIRECTION_LTR
                            }
                        }
                    }
                    val mLightLockScreenWallpaper = fldLightLockScreenWallpaper?.getBoolean(miuiKeyguardStatusBarView) == true
                    val mDep = fldDep?.get(miuiKeyguardStatusBarView)
                    val lightModeIconColorSingleTone = XposedHelpers.getObjectField(mDep, "iconDispatcher")?.let { iconDispatcher ->
                        XposedHelpers.callMethod(iconDispatcher, "getLightModeIconColorSingleTone")
                    }
                    val createMiuiIconManager = fldIconManagerFactory?.get(miuiKeyguardStatusBarView)?.let { iconFactory ->
                        XposedHelpers.callMethod(
                            iconFactory,
                            "createMiuiIconManager",
                            leftStatusIcons,
                            enumStatusBarLocationKeyguard,
                            mLightLockScreenWallpaper,
                            lightModeIconColorSingleTone
                        )
                    }
                    miuiKeyguardStatusBarView.setAdditionalInstanceField(KEY_LEFT_STATUS_ICON_MANAGER, createMiuiIconManager)
                    fldIconController?.get(miuiKeyguardStatusBarView)?.let {
                        metAddIconGroup?.invoke(it, createMiuiIconManager)
                    }
                    metSetIgnoredSlots?.invoke(leftStatusIcons, leftBlockList)
                    metUpdateLayoutFrom?.invoke(leftStatusIcons, 1)
                    val islandController = mDep?.let {
                        XposedHelpers.getObjectField(it, "islandController")
                    }
                    metSetIslandController?.invoke(
                        leftStatusIcons,
                        islandController,
                        1
                    )
                }
            }
        }
        clzMiuiKeyguardStatusBarView?.apply {
            val fldInit = resolve().optional(true).firstFieldOrNull {
                name = "mInit"
            }?.self?.apply { makeAccessible() }
            val fldShowCarrier = resolve().optional(true).firstFieldOrNull {
                name = "mShowCarrier"
            }?.self?.apply { makeAccessible() }
            val fldStatusIconContainer = resolve().optional(true).firstFieldOrNull {
                name = "mStatusIconContainer"
                superclass()
            }?.self?.apply { makeAccessible() }
            var hookInit = false
            resolve().firstMethodOrNull {
                name = "initCallback"
            }?.hook {
                after {
                    if (!hookInit && fldInit?.get(this.instance) == true) {
                        val view = this.instance<View>()
                        val leftStatusIconContainer = getOrPutAdditionalInstanceField(this.instance, view.context, false) ?: return@after
                        metSetNeedLimitIcon?.invoke(leftStatusIconContainer, true)
                        fldStatusIconContainer?.get(this.instance)?.let { container ->
                            fldAnimatable?.get(container)?.let {
                                metSetAnimatable?.invoke(leftStatusIconContainer, it)
                            }
                            fldAnimatorController?.get(container)?.let {
                                metSetAnimatorController?.invoke(leftStatusIconContainer, it)
                            }
                        }
                        hookInit = true
                    }
                }
            }
            resolve().firstMethodOrNull {
                name {
                    it.startsWith("updateCarrierVisibility")
                }
            }?.hook {
                after {
                    val view = this.instance<View>()
                    val leftStatusIconContainer = getOrPutAdditionalInstanceField(this.instance, view.context, false) ?: return@after
                    leftStatusIconContainer.visibility =
                        if (fldShowCarrier?.getBoolean(this.instance) == true) View.VISIBLE
                        else View.GONE
                }
            }
            val fldTintedIconManager = resolve().firstFieldOrNull {
                name = "mTintedIconManager"
                superclass()
            }?.self?.apply { makeAccessible() }
            val clzMiuiLightDarkIconManager = "com.android.systemui.statusbar.phone.MiuiLightDarkIconManager".toClassOrNull()
            val metSetLight = clzMiuiLightDarkIconManager?.resolve()?.firstMethodOrNull {
                name = "setLight"
                parameters(Int::class, Boolean::class, Boolean::class)
            }?.self?.apply { makeAccessible() }
            val fldColor = clzMiuiLightDarkIconManager?.resolve()?.firstFieldOrNull {
                name = "mColor"
                type(Int::class)
            }?.self?.apply { makeAccessible() }
            val fldLight = clzMiuiLightDarkIconManager?.resolve()?.firstFieldOrNull {
                name = "mLight"
                type(Boolean::class)
            }?.self?.apply { makeAccessible() }
            resolve().firstMethodOrNull {
                name = "updateIconsAndTextColors"
            }?.hook {
                after {
                    val leftStatusIconManager = this.instance.getAdditionalInstanceField<Any>(KEY_LEFT_STATUS_ICON_MANAGER) ?: return@after
                    val mTintedIconManager = fldTintedIconManager?.get(this.instance) ?: return@after
                    val mColor = fldColor?.getInt(mTintedIconManager) ?: return@after
                    val mLight = fldLight?.getBoolean(mTintedIconManager) ?: return@after
                    metSetLight?.invoke(leftStatusIconManager, mColor, mLight, false)
                }
            }
        }
    }

    private fun getOrPutAdditionalInstanceField(obj: Any, context: Context, remember: Boolean): View? {
        return obj.getAdditionalInstanceField<View>(KEY_LEFT_STATUS_ICON_CONTAINER)
            ?: ctorMiuiStatusIconContainer?.createAsType<LinearLayout>(context)?.also {
                obj.setAdditionalInstanceField(KEY_LEFT_STATUS_ICON_CONTAINER, it)
                if (remember) leftContainers.add(it)
            }
    }
}