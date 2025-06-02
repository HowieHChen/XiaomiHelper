package dev.lackluster.mihelper.hook.rules.systemui.lockscreen

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.TextAppearance_StatusBar_Clock
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.keyguard_carrier_text
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_carrier_second_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_carrier_vertical_separator
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.normal_control_center_carrier_view
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_clock_margin_end
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.status_bar_padding_extra_start
import dev.lackluster.mihelper.hook.rules.systemui.statusbar.ElementsFontWeight.fontPath
import dev.lackluster.mihelper.hook.view.SpringInterpolator
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.dp

object CarrierTextView : YukiBaseHooker() {
    private const val CUSTOM_VIEW_ID = 0x00111111
    private const val KEY_ANIM_TO_AOD = "mAnimToAod"
    private const val KEY_ANIMATOR = "mAnimator"
    private const val KEY_DARK_INTENSITY = "mDarkIntensity"
    private const val KEY_LIGHT_COLOR = "mLightColor"
    private const val KEY_DARK_COLOR = "mDarkColor"

    private val carrierTextType = Prefs.getInt(Pref.Key.SystemUI.LockScreen.CARRIER_TEXT, 0)
    private val carrierFont = Prefs.getBoolean(Pref.Key.SystemUI.FontWeight.CARRIER, false)
    private val carrierFontWeight = Prefs.getInt(Pref.Key.SystemUI.FontWeight.CARRIER_WEIGHT, 430)

    private val miuiCarrierTextLayoutClass by lazy {
        "com.android.systemui.controlcenter.shade.MiuiCarrierTextLayout".toClassOrNull()
    }
    private val darkIconDispatcherClass by lazy {
        "com.android.systemui.plugins.DarkIconDispatcher".toClass()
    }
    private val miuiClockClass by lazy {
        "com.android.systemui.statusbar.views.MiuiClock".toClass()
    }
    private val isNewCarrierTextLayout by lazy {
        miuiCarrierTextLayoutClass != null
    }


    override fun onHook() {
        if (carrierTextType == 1 && isNewCarrierTextLayout) {
            miuiCarrierTextLayoutClass?.apply {
                method {
                    name = "onDensityOrFontScaleChanged"
                }.hook {
                    after {
                        val wght = if (carrierFont) carrierFontWeight else 430
                        for (fieldName in listOf("leftCarrierTextView", "rightCarrierTextView")) {
                            this.instance.current().field {
                                name = fieldName
                            }.cast<TextView>()?.apply {
                                setTextAppearance(TextAppearance_StatusBar_Clock)
                                isHorizontalFadingEdgeEnabled = true
                                typeface = Typeface.Builder(fontPath)
                                    .setFontVariationSettings("'wght' $wght")
                                    .build()
                            }
                        }
                    }
                }
            }
        }
        if (carrierTextType != 0) {
            "com.android.systemui.statusbar.phone.MiuiKeyguardStatusBarView".toClass().apply {
                method {
                    name = "animateFullAod"
                }.ignored().hook {
                    after {
                        val viewGroup = this.instance<ViewGroup>()
                        val toLock = this.args(0).boolean()
                        val spring = SpringInterpolator(0.95f, 0.35f)
                        val isDark = this.instance.current().field {
                            name = "mIsDark"
                            superClass()
                        }.boolean()
                        val animFlag: Boolean
                        if (!toLock && isDark) {
                            XposedHelpers.setAdditionalInstanceField(viewGroup, KEY_ANIM_TO_AOD, true)
                            animFlag = false
                            // doAnimateColor(false)
                        } else if (toLock && XposedHelpers.getAdditionalInstanceField(viewGroup, KEY_ANIM_TO_AOD) == true) {
                            animFlag = true
                            // doAnimateColor(true)
                            XposedHelpers.setAdditionalInstanceField(viewGroup, KEY_ANIM_TO_AOD, false)
                        } else {
                            return@after
                        }
                        (XposedHelpers.getAdditionalInstanceField(viewGroup, KEY_ANIMATOR) as? ValueAnimator)?.cancel()
                        val animator =
                            if (animFlag) ValueAnimator.ofFloat(0.0f, 1.0f)
                            else ValueAnimator.ofFloat(1.0f, 0.0f)
                        animator.apply {
                            interpolator = spring
                            duration = spring.duration
                            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                                private var mLastV = -1.0f
                                override fun onAnimationUpdate(animation: ValueAnimator) {
                                    val value = (animation.animatedValue as? Float ?: return).coerceIn(0.0f, 1.0f)
                                    if (value == mLastV) return
                                    if (value != 0.0f && value != 1.0f) {
                                        val f = (XposedHelpers.getAdditionalInstanceField(viewGroup, KEY_DARK_INTENSITY) as? Float ?: 0.0f) - value
                                        if (f <= 0.01f && f >= -0.01f) {
                                            return
                                        }
                                    }
                                    val mLightColor = XposedHelpers.getAdditionalInstanceField(viewGroup, KEY_LIGHT_COLOR) as? Int ?: Color.WHITE
                                    val mDarkColor = XposedHelpers.getAdditionalInstanceField(viewGroup, KEY_DARK_COLOR) as? Int ?: Color.BLACK
                                    val mTintColor = ArgbEvaluator().evaluate(
                                        value,
                                        mLightColor,
                                        if (isDark) mDarkColor else mLightColor
                                    ) as Int
                                    val tintColorStateList = ColorStateList.valueOf(mTintColor)
                                    for (viewId in listOf(
                                        CUSTOM_VIEW_ID, normal_control_center_carrier_view, normal_control_center_carrier_second_view
                                    )) {
                                        viewGroup.findViewById<TextView>(viewId)?.let {
                                            TextViewCompat.setCompoundDrawableTintList(it, tintColorStateList)
                                            it.setTextColor(mTintColor)
                                        }
                                    }
                                    viewGroup.findViewById<ImageView>(normal_control_center_carrier_vertical_separator)?.setColorFilter(mTintColor)
                                    XposedHelpers.setAdditionalInstanceField(viewGroup, KEY_DARK_INTENSITY, value)
                                }
                            })
                        }
                        XposedHelpers.setAdditionalInstanceField(viewGroup, KEY_ANIMATOR, animator)
                        animator.start()
                    }
                }
                method {
                    name = "onFinishInflate"
                }.hook {
                    after {
                        val viewGroup = this.instance<ViewGroup>()
                        val targetView = viewGroup.findViewById<TextView>(keyguard_carrier_text) ?: return@after
                        val context = viewGroup.context
                        val parent = targetView.parent as? ViewGroup ?: return@after
                        if (carrierTextType == 1 && isNewCarrierTextLayout) {
                            val miuiCarrierTextLayout = miuiCarrierTextLayoutClass!!.constructor {
                                paramCount = 1
                            }.get().newInstance<LinearLayout>(context)
                            if (miuiCarrierTextLayout == null) {
                                YLog.warn("Can't create instance of MiuiCarrierTextLayout")
                                targetView.visibility = View.VISIBLE
                                return@after
                            }
                            val wght = if (carrierFont) carrierFontWeight else 430
                            for (fieldName in listOf("leftCarrierTextView", "rightCarrierTextView")) {
                                miuiCarrierTextLayout.current().field {
                                    name = fieldName
                                }.cast<TextView>()?.apply {
                                    setTextAppearance(TextAppearance_StatusBar_Clock)
                                    isHorizontalFadingEdgeEnabled = true
                                    typeface = Typeface.Builder(fontPath)
                                        .setFontVariationSettings("'wght' $wght")
                                        .build()
                                }
                            }
                            miuiCarrierTextLayout.current().field {
                                name = "carrierTextStyleResId"
                            }.set(TextAppearance_StatusBar_Clock)
                            parent.addView(
                                miuiCarrierTextLayout,
                                parent.indexOfChild(targetView),
                                ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            )
                            parent.removeView(targetView)
                        } else if (carrierTextType == 1) {
                            targetView.visibility = View.VISIBLE
                            targetView.id = CUSTOM_VIEW_ID
                        } else {
                            val clockContainer = LinearLayout(context).apply {
                                clipChildren = false
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                            val padClock = LayoutInflater.from(context).inflate(ResourcesUtils.pad_clock_xml, null) as TextView
                            val clockView = miuiClockClass.constructor {
                                paramCount = 3
                            }.get().newInstance<TextView>(context, null, -1)
                            if (clockView == null) {
                                YLog.warn("Can't create instance of MiuiClock")
                                return@after
                            }
                            clockView.setTextAppearance(TextAppearance_StatusBar_Clock)
                            clockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, padClock.textSize)
                            clockView.typeface = padClock.typeface
                            clockView.gravity = Gravity.CENTER or Gravity.START
                            clockView.isSingleLine = true
                            clockView.id = CUSTOM_VIEW_ID
                            clockView.tag = CUSTOM_VIEW_ID
                            val clockMarginStart = status_bar_padding_extra_start.takeIf { it != 0 }?.let {
                                context.resources.getDimensionPixelSize(it)
                            } ?: 0
                            val clockMarginEnd = status_bar_clock_margin_end.takeIf { it != 0 }?.let {
                                context.resources.getDimensionPixelSize(it)
                            } ?: 4.0f.dp(context)
                            clockContainer.addView(
                                clockView,
                                ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                ).apply {
                                    marginStart = clockMarginStart
                                    marginEnd = clockMarginEnd
                                }
                            )
                            if (Device.isPad) {
                                clockContainer.addView(
                                    padClock,
                                    ViewGroup.MarginLayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    ).apply {
                                        marginStart = clockMarginStart
                                        marginEnd = clockMarginEnd
                                    }
                                )
                            }
                            parent.addView(
                                clockContainer,
                                parent.indexOfChild(targetView)
                            )
                            parent.removeView(targetView)
                        }
                    }
                }
                method {
                    name = "updateIconsAndTextColors"
                }.hook {
                    after {
                        val isDark = this.instance.current().field {
                            name = "mIsDark"
                            superClass()
                        }.boolean()
                        val sDependency = "com.android.systemui.Dependency".toClass().field {
                            name = "sDependency"
                            modifiers { isStatic }
                        }.get().any()
                        val darkIconDispatcher = XposedHelpers.callMethod(
                            sDependency,
                            "getDependencyInner",
                            darkIconDispatcherClass
                        )
                        val lightModeIconColorSingleTone = XposedHelpers.callMethod(
                            darkIconDispatcher,
                            "getLightModeIconColorSingleTone"
                        ) as? Int ?: Color.WHITE
                        val darkModeIconColorSingleTone = XposedHelpers.callMethod(
                            darkIconDispatcher,
                            "getDarkModeIconColorSingleTone"
                        ) as? Int ?: Color.BLACK
                        val tintColor = if (isDark) darkModeIconColorSingleTone else lightModeIconColorSingleTone
                        val viewGroup = this.instance<ViewGroup>()
                        val tintColorStateList = ColorStateList.valueOf(tintColor)
                        for (viewId in listOf(
                            CUSTOM_VIEW_ID, normal_control_center_carrier_view, normal_control_center_carrier_second_view
                        )) {
                            viewGroup.findViewById<TextView>(viewId)?.let {
                                TextViewCompat.setCompoundDrawableTintList(it, tintColorStateList)
                                it.setTextColor(tintColor)
                            }
                        }
                        viewGroup.findViewById<ImageView>(normal_control_center_carrier_vertical_separator)?.setColorFilter(tintColor)
                        XposedHelpers.setAdditionalInstanceField(viewGroup, KEY_LIGHT_COLOR, lightModeIconColorSingleTone)
                        XposedHelpers.setAdditionalInstanceField(viewGroup, KEY_DARK_COLOR, darkModeIconColorSingleTone)
                    }
                }
            }
        }
    }
}