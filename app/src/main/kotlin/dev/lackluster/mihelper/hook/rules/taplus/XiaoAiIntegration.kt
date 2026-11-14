/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2026 HowieHChen, howie.dev@outlook.com
 */

package dev.lackluster.mihelper.hook.rules.taplus

import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.postDelayed
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.ACTION_PIN_TO_REAR_SCREEN
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.miai.RearScreenPin
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.card_recommend_recognition_margin
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.expanded_card_margin
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.icon_text
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.item_card_margin_right
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.recognition_card_2
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.recognition_shopping
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.recognition_translate
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.recommend_group
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.shrink_card_elevation
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.shrink_card_height
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.shrink_card_width
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.size_50_for_j18
import dev.lackluster.mihelper.hook.rules.taplus.ResourcesUtils.size_60
import dev.lackluster.mihelper.hook.utils.CommonGesture
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.hook.utils.d
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device

object XiaoAiIntegration : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private const val REAR_SCREEN_IMAGE_DELETE_DELAY_MS = 10_000L

    private val clzTaplusSplashFloatView by "com.miui.contentextension.text.floatview.TaplusSplashFloatView".lazyClassOrNull()
    private val metInitRecognitionCard by lazy {
        clzTaplusSplashFloatView?.resolve()?.firstMethodOrNull {
            name = "initRecognitionCard"
            parameterCount = 1
        }?.toTyped<Unit>()
    }
    private val metHideSplashFloatView by lazy {
        clzTaplusSplashFloatView?.resolve()?.firstMethodOrNull {
            name = "hideSplashFloatView"
            parameters(Boolean::class)
        }?.toTyped<Unit>()
    }

    private val clzTaplusRecognitionShrinkCard by "com.miui.contentextension.text.cardview.TaplusRecognitionShrinkCard".lazyClassOrNull()
    private val ctorTaplusRecognitionShrinkCard by lazy {
        clzTaplusRecognitionShrinkCard?.resolve()?.firstConstructorOrNull {
            parameterCount = 2
        }?.toTyped()
    }
    private val fldRecognitionIcon by lazy {
        clzTaplusRecognitionShrinkCard?.resolve()?.firstFieldOrNull {
            name = "mRecognitionIcon"
        }?.toTyped<ImageView>()
    }
    private val fldRecognitionText by lazy {
        clzTaplusRecognitionShrinkCard?.resolve()?.firstFieldOrNull {
            name = "mRecognitionText"
        }?.toTyped<TextView>()
    }
    private val metSetType by lazy {
        clzTaplusRecognitionShrinkCard?.resolve()?.firstMethodOrNull {
            name = "setType"
            parameters(Int::class)
        }?.toTyped<Unit>()
    }

    private val FLOAT_IN_INTERPOLATOR by lazy {
        "com.miui.contentextension.utils.FloatViewAnimUtil".toClassOrNull()?.resolve()?.firstFieldOrNull {
            name = "FLOAT_IN_INTERPOLATOR"
            modifiers(Modifiers.STATIC)
        }?.toTyped<TimeInterpolator>()?.get(null)
    }

    private val metIsLanguageInZH by lazy {
        "com.miui.contentextension.utils.DeviceConfig".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "isLanguageInZH"
            parameters(Context::class)
            modifiers(Modifiers.STATIC)
        }?.toTyped<Boolean>()
    }

    private val metGetBitmap by lazy {
        "com.miui.contentextension.services.TextContentExtensionService".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "getBitmap"
            parameterCount = 0
            modifiers(Modifiers.STATIC)
        }?.toTyped<Bitmap>()
    }

    private var View.mRecognitionCard3 by extraOf<LinearLayout>("XIAOAI_RECOGNITION_CARD", null)
    private var View.textRearScreenAction by extraOf<TextView>("TEXT_REAR_SCREEN_ACTION", null)

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.Taplus.XIAOAI_INTEGRATION.observe {
                updateSelfState(it)
            }
        )
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onStart() {
        // 小爱识屏卡片
        "com.miui.contentextension.text.floatview.TaplusSplashFloatView".toClassOrNull()?.apply {
            val fldMainView = resolve().firstFieldOrNull {
                name = "mMainView"
                superclass()
            }?.toTyped<View>()
            resolve().firstMethodOrNull {
                name = "initRecognitionCards"
                parameterCount = 0
            }?.hook {
                val ori = proceed()
                val mMainView = fldMainView?.get(thisObject)
                if (mMainView != null && mMainView.mRecognitionCard3 == null) {
                    val newCard = createXiaoAiCard(thisObject, mMainView)
                    mMainView.mRecognitionCard3 = newCard
                    val recognition2 = mMainView.findViewById<LinearLayout>(recognition_card_2)
                    val recommendGroup = mMainView.findViewById<LinearLayout>(recommend_group)
                    val parent = recognition2.parent as? RelativeLayout
                    d { "initRecognitionCards newCard $newCard recognition2 $recognition2 recommendGroup $recommendGroup parent $parent" }
                    if (newCard != null && parent != null && recommendGroup.parent == parent) {
                        val resources = parent.context.resources
                        val lp = RelativeLayout.LayoutParams(
                            resources.getDimensionPixelSize(shrink_card_width),
                            resources.getDimensionPixelSize(shrink_card_height),
                        ).apply {
                            addRule(RelativeLayout.BELOW, recognition_card_2)
                            topMargin = resources.getDimensionPixelSize(card_recommend_recognition_margin)
                            addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                        }
                        d { "initRecognitionCards layoutParams ${recommendGroup.layoutParams}" }
                        parent.addView(newCard, lp)
                        parent.removeView(recommendGroup)
                        val lp2 = RelativeLayout.LayoutParams(
                            resources.getDimensionPixelSize(shrink_card_width),
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                        ).apply {
                            addRule(RelativeLayout.BELOW, newCard.id)
                            topMargin = resources.getDimensionPixelSize(expanded_card_margin)
                            bottomMargin = resources.getDimensionPixelSize(expanded_card_margin)
                            addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                        }
                        parent.addView(recommendGroup, lp2)
                    }
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "showRecognitionCards"
                parameterCount = 0
            }?.hook {
                val ori = proceed()
                val mMainView = fldMainView?.get(thisObject)
                val mRecognitionCard3 = mMainView?.mRecognitionCard3
                mRecognitionCard3?.postDelayed(325L) {
                    d { "showRecognitionCards postDelayed run" }
                    mRecognitionCard3.translationX = mRecognitionCard3.measuredWidth.toFloat()
                    mRecognitionCard3.visibility = View.VISIBLE
                    val transX = mRecognitionCard3.context.resources.getDimensionPixelSize(item_card_margin_right).toFloat()
                    mRecognitionCard3.animate()
                        .translationX(-transX)
                        .setInterpolator(FLOAT_IN_INTERPOLATOR)
                        .setDuration(350L)
                        .start()
                }
                result(ori)
            }
        }
        if (!Device.isIndependentRearDeviceAndSupportAssistant) return
        // 背屏贴文字
        val metDoAlpha = "".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "doAlpha"
            parameters(View::class)
            modifiers(Modifiers.STATIC)
        }?.toTyped<Unit>()
        "com.miui.contentextension.text.cardview.TaplusRecognitionExpandedTextCard".toClassOrNull()?.apply {
            val setOperationEnabled: (TextView, Boolean) -> Unit = { textView, enabled ->
                textView.isEnabled = enabled
                textView.alpha = if (enabled) 1.0f else 0.3f
            }
            val fldCopy = resolve().firstFieldOrNull {
                name = "mCopy"
            }?.toTyped<TextView>()
            val fldSegmentAdapter = resolve().firstFieldOrNull {
                name = "mSegmentAdapter"
            }?.toTyped<Any>()
            val metGetSelectedWords = "com.miui.contentextension.text.adapter.TaplusSegmentAdapter".toClassOrNull()?.resolve()?.firstMethodOrNull {
                name = "getSelectedWords"
            }?.toTyped<String>()
            resolve().firstConstructorOrNull {
                parameterCount = 3
            }?.hook {
                val ori = proceed()
                (thisObject as? View)?.let { card ->
                    if (card.textRearScreenAction != null) return@let
                    val translate = card.findViewById<TextView>(recognition_translate)
                    val parent = translate?.parent as? RelativeLayout ?: return@let

                    val action = TextView(translate.context).apply {
                        id = View.generateViewId()
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, translate.textSize)
                        setTextColor(translate.textColors)
                        typeface = translate.typeface
                        gravity = translate.gravity
                        isSingleLine = true
                        ellipsize = translate.ellipsize
                        compoundDrawablePadding = translate.compoundDrawablePadding

                        val moduleRes = context.packageManager.getResourcesForApplication(BuildConfig.APPLICATION_ID)
                        if (metIsLanguageInZH?.invoke(null, context) == true) {
                            text = moduleRes.getString(R.string.others_taplus_rear_screen)
                        }
                        val icon = moduleRes.getDrawable(R.drawable.ic_rear_screen)
                        val size = context.resources.getDimensionPixelSize(size_60)
                        icon.setBounds(0, 0, size, size)
                        setCompoundDrawablesRelative(icon, null, null, null)

                        setOnClickListener {
                            val text = fldSegmentAdapter?.get(card)?.let { adapter ->
                                metGetSelectedWords?.invoke(adapter)
                            }
                            text?.takeIf(String::isNotBlank)?.let { selected ->
                                sendRearScreenPin(context.applicationContext, RearScreenPin.TYPE_TEXT, selected)
                            }
                        }
                        setOperationEnabled(this, translate.isEnabled)
                        metDoAlpha?.invoke(null, this)
                    }

                    translate.layoutParams = (translate.layoutParams as? RelativeLayout.LayoutParams)?.apply {
                        marginEnd = translate.context.resources.getDimensionPixelSize(size_50_for_j18)
                    }

                    val layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        addRule(RelativeLayout.END_OF, translate.id)
                    }
                    parent.addView(action, layoutParams)

                    card.textRearScreenAction = action
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "onSearchChanged"
                parameters(Boolean::class)
            }?.hook {
                val ori = proceed()
                (thisObject as? View)?.textRearScreenAction?.let {
                    setOperationEnabled(it, fldCopy?.get(thisObject)?.isEnabled == true)
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "enableOperations"
                parameters(Boolean::class)
            }?.hook {
                val ori = proceed()
                (thisObject as? View)?.textRearScreenAction?.let {
                    setOperationEnabled(it, fldCopy?.get(thisObject)?.isEnabled == true)
                }
                result(ori)
            }
        }
        // 背屏贴图片
        "com.miui.contentextension.text.cardview.TaplusRecognitionExpandedImageCard".toClassOrNull()?.apply {
            val fldShopping = resolve().firstFieldOrNull {
                name = "mShopping"
            }?.toTyped<TextView>()
            resolve().firstMethodOrNull {
                name = "updateLayout"
            }?.hook {
                val ori = proceed()
                fldShopping?.get(thisObject)?.apply {
                    val moduleRes = context.packageManager.getResourcesForApplication(BuildConfig.APPLICATION_ID)
                    visibility = View.VISIBLE
                    if (metIsLanguageInZH?.invoke(null, context) == true) {
                        text = moduleRes.getString(R.string.others_taplus_rear_screen)
                    }
                    val icon = moduleRes.getDrawable(R.drawable.ic_rear_screen)
                    val size = context.resources.getDimensionPixelSize(size_60)
                    icon.setBounds(0, 0, size, size)
                    setCompoundDrawablesRelative(icon, null, null, null)
                }
                result(ori)
            }
            resolve().firstMethodOrNull {
                name = "onClick"
                parameters(View::class)
            }?.hook {
                val view = getArg(0) as? View
                if (view?.id == recognition_shopping) {
                    metGetBitmap?.invoke(null)?.let { bitmap ->
                        val appContext = view.context.applicationContext
                        Thread {
                            saveBitmapForRearScreen(appContext, bitmap)?.let { uri ->
                                sendRearScreenPin(appContext, RearScreenPin.TYPE_IMAGE, imageUri = uri)
                                scheduleRearScreenImageCleanup(appContext, uri)
                            }
                        }.start()
                    }
                    result(null)
                } else {
                    result(proceed())
                }
            }
        }
    }

    private fun createXiaoAiCard(floatView: Any, mainView: View): LinearLayout? {
        val newCard = ctorTaplusRecognitionShrinkCard?.newInstance(mainView.context, null) as? LinearLayout ?: return null
        newCard.elevation = mainView.context.resources.getDimension(shrink_card_elevation)
        newCard.orientation = LinearLayout.HORIZONTAL
        newCard.visibility = View.GONE
        newCard.id = View.generateViewId()
        runCatching {
            metSetType?.invoke(newCard, 2)
            metInitRecognitionCard?.invoke(floatView, newCard)
            val appInfo = mainView.context.packageManager.getApplicationInfo(Scope.MI_AI, 0)
            fldRecognitionIcon?.get(newCard)?.setImageDrawable(mainView.context.packageManager.getApplicationIcon(appInfo))
            fldRecognitionText?.get(newCard)?.text = mainView.context.packageManager.getApplicationLabel(appInfo)
        }.onFailure {
            metSetType?.invoke(newCard, 0)
            metInitRecognitionCard?.invoke(floatView, newCard)
            metSetType?.invoke(newCard, 2)
            fldRecognitionIcon?.get(newCard)?.setImageResource(icon_text)
            fldRecognitionText?.get(newCard)?.text = "超级小爱"
        }
        newCard.setOnClickListener(null)
        newCard.setOnClickListener {
            metHideSplashFloatView?.invoke(floatView, true)
            mainView.postDelayed(210L) {
                CommonGesture.doAction(it.context.applicationContext, 7)
            }
        }
        return newCard
    }

    private fun saveBitmapForRearScreen(context: Context, bitmap: Bitmap) = runCatching {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "taplus_rear_screen_${System.currentTimeMillis()}.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/HyperHelper")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create rear-screen image URI")
        try {
            resolver.openOutputStream(uri)?.use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                    "Unable to encode rear-screen image"
                }
            } ?: error("Unable to open rear-screen image URI")
            resolver.update(uri, ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }, null, null)
            uri
        } catch (throwable: Throwable) {
            resolver.delete(uri, null, null)
            throw throwable
        }
    }.onFailure {
        d { "Failed to save Taplus image for rear screen: $it" }
    }.getOrNull()

    private fun sendRearScreenPin(
        context: Context,
        type: String,
        text: String? = null,
        imageUri: android.net.Uri? = null,
    ) {
        runCatching {
            context.startService(
                Intent(ACTION_PIN_TO_REAR_SCREEN).apply {
                    setClassName(Scope.MI_AI, "com.xiaomi.voiceassistant.VoiceService")
                    putExtra(RearScreenPin.EXTRA_TYPE, type)
                    text?.let { putExtra(RearScreenPin.EXTRA_TEXT, it) }
                    imageUri?.let { uri ->
                        putExtra(RearScreenPin.EXTRA_IMAGE_URI, uri)
                        clipData = ClipData.newRawUri("rear-screen-image", uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                },
            )
        }.onFailure {
            d { "Failed to send rear-screen pin request: $it" }
        }
    }

    private fun scheduleRearScreenImageCleanup(context: Context, uri: android.net.Uri) {
        Handler(Looper.getMainLooper()).postDelayed({
            runCatching {
                context.contentResolver.delete(uri, null, null)
            }.onFailure {
                d { "Failed to delete temporary rear-screen image $uri: $it" }
            }
        }, REAR_SCREEN_IMAGE_DELETE_DELAY_MS)
    }
}
