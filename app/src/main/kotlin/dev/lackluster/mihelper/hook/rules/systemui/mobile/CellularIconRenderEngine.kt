package dev.lackluster.mihelper.hook.rules.systemui.mobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.PointF
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.os.ParcelFileDescriptor
import android.text.TextPaint
import android.util.LruCache
import android.view.View
import androidx.core.graphics.createBitmap
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.utils.MLog
import dev.lackluster.mihelper.utils.StackedMobileIconUtils
import dev.lackluster.mihelper.utils.SystemProperties
import kotlin.collections.set
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object CellularIconRenderEngine {
    private const val TAG = "CellularIconRenderEngine"

    private val typeFontMode by Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE.lazyGet()
    private val typeWidthCondensed by lazy {
        Preferences.SystemUI.StatusBar.StackedMobile.TYPE_WIDTH_CONDENSED.get().coerceIn(10, 200)
    }

    private val standaloneTypeSizeDp by Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_SIZE.lazyGet()
    private val standaloneTypePaddingStartDp by Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_START_VAL.lazyGet()
    private val standaloneTypePaddingEndDp by Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_END_VAL.lazyGet()
    private val standaloneTypeVerticalOffsetDp by Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_VERTICAL_OFFSET.lazyGet()
    private val standaloneTypeFontWeight by lazy {
        Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_FONT_WEIGHT.get().coerceIn(1..1000)
    }

    private val smallTypeSizeDp by Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SIZE.lazyGet()
    private val smallTypeFontWeight by lazy {
        Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_FONT_WEIGHT.get().coerceIn(1..1000)
    }

    private var typefaceStandaloneTypeNormal = Typeface.DEFAULT_BOLD
    private var typefaceStandaloneTypeCondensed = Typeface.DEFAULT_BOLD
    private var typefaceSmallTypeNormal = Typeface.DEFAULT_BOLD
    private var typefaceSmallTypeCondensed = Typeface.DEFAULT_BOLD

    private val isStandaloneTypeAutoSpecialOpt by lazy {
        (typeFontMode == 2 || typeFontMode == 3)
    }

    @Volatile
    var isPreloaded = false // 对外公开这个状态
        private set

    // L1: 矢量层，不受分辨率影响，永久缓存 (占用内存极小)
    private val vectorCache = HashMap<String, Picture>(42)

    // L2: 光栅化层，受分辨率影响
    private val signalBitmapCache = HashMap<String, Bitmap>(42)
    private val smallTypeBitmapCache = HashMap<String, Bitmap>(16)
    private val standaloneTypeIconCache = HashMap<String, Icon>(16)

    // L3: 终极成品库
    // Key 为完美的图纸对象，Value 为组合好的 Icon。容量设为 30 足以应付高频状态切换
    private val finalIconCache = LruCache<CellularIconState, Icon>(30)

    private var singleTypeCenterPercent: PointF? = null
    private var stackedTypeCenterPercent: PointF? = null
    // 记录生成当前 L2 缓存时的系统 DPI
    private var currentDpi = -1
    private var currentDensity = 1.0f
    private var currentIconHeightPx = 20
    private var statusBarHeightResId = 0

    fun preload(
        context: Context,
        iconHeightResId: Int,
        remoteFontFd: ParcelFileDescriptor?,
        customSingleSvg: String?,
        customStackedSvg: String?,
    ): Boolean {
        if (isPreloaded) return false
        synchronized(this) {
            if (isPreloaded) return false
            this.statusBarHeightResId = iconHeightResId
            ensureEnvironment(context, forceUpdate = true)

            initTypefaces(context, remoteFontFd)
            val vectorLoaded = initVectorPictures(context, customSingleSvg, customStackedSvg)

            "4G,4G+,LTE,5G,5G+,5GA".split(",").forEach {
                getL2SmallTypeBitmap(it)
                renderStandalone(
                    CellularIconState.StandaloneNetType(it),
                    context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
                )
            }

            isPreloaded = true
            return vectorLoaded
        }
    }

    private fun ensureEnvironment(context: Context, forceUpdate: Boolean = false) {
        val newDpi = context.resources.displayMetrics.densityDpi
        if (forceUpdate || newDpi != currentDpi) {
            // DPI 变了（比如用户在系统设置里改了显示大小），立刻清空光栅缓存！
            signalBitmapCache.clear()
            smallTypeBitmapCache.clear()
            standaloneTypeIconCache.clear()
            finalIconCache.evictAll() // L3 清理

            currentDpi = newDpi
            currentDensity = context.resources.displayMetrics.density
            if (statusBarHeightResId != 0) {
                // 只有在这里才去查 Resource 表！性能大幅提升！
                currentIconHeightPx = context.resources.getDimensionPixelSize(statusBarHeightResId)
            }
        }
    }

    private fun initTypefaces(context: Context, remoteFontFd: ParcelFileDescriptor?) {
        var fallbackToDefaultFont = false
        if (typeFontMode == 1) {
            try {
                if (remoteFontFd != null) {
                    typefaceStandaloneTypeNormal = Typeface.Builder(remoteFontFd.fileDescriptor)
                        .setFontVariationSettings("'wght' $standaloneTypeFontWeight")
                        .build()
                    typefaceSmallTypeNormal = Typeface.Builder(remoteFontFd.fileDescriptor)
                        .setFontVariationSettings("'wght' $smallTypeFontWeight")
                        .build()
                } else {
                    val defaultPath = SystemProperties.get("ro.miui.ui.font.mi_font_path", VARIABLE_FONT_DEFAULT_PATH)
                    typefaceStandaloneTypeNormal = Typeface.Builder(defaultPath)
                        .setFontVariationSettings("'wght' $standaloneTypeFontWeight")
                        .build()
                    typefaceSmallTypeNormal = Typeface.Builder(defaultPath)
                        .setFontVariationSettings("'wght' $smallTypeFontWeight")
                        .build()
                }
            } catch (t: Throwable) {
                fallbackToDefaultFont = true
                MLog.e(TAG, t) { "Error parsing font from RemoteFile" }
            }
        } else if (typeFontMode != 0) {
            try {
                val moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                val fontFileName = if (typeFontMode == 2) Constants.ASSETS_VF_MI_SANS_CONDENSED else Constants.ASSETS_VF_SF_PRO
                typefaceStandaloneTypeNormal = Typeface.Builder(moduleContext.assets, fontFileName)
                    .setFontVariationSettings("'wght' $standaloneTypeFontWeight, 'wdth' 100")
                    .build()
                typefaceStandaloneTypeCondensed = Typeface.Builder(moduleContext.assets, fontFileName)
                    .setFontVariationSettings("'wght' $standaloneTypeFontWeight, 'wdth' $typeWidthCondensed")
                    .build()
                typefaceSmallTypeNormal = Typeface.Builder(moduleContext.assets, fontFileName)
                    .setFontVariationSettings("'wght' $smallTypeFontWeight, 'wdth' 100")
                    .build()
                typefaceSmallTypeCondensed = Typeface.Builder(moduleContext.assets, fontFileName)
                    .setFontVariationSettings("'wght' $smallTypeFontWeight, 'wdth' $typeWidthCondensed")
                    .build()
            } catch (t: Throwable) {
                fallbackToDefaultFont = true
                MLog.e(TAG, t) { "Error occurred while retrieving built-in font resources" }
            }
        }
        if (fallbackToDefaultFont) {
            typefaceStandaloneTypeNormal = Typeface.DEFAULT_BOLD
            typefaceSmallTypeNormal = Typeface.DEFAULT_BOLD
        }
    }

    private fun initVectorPictures(
        context: Context,
        customSingleSvg: String?,
        customStackedSvg: String?
    ): Boolean {
        val moduleContext = try {
            context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
        } catch (e: Exception) {
            MLog.e(TAG, e) { "Error occurred while retrieving built-in svg resources" }
            null
        }

        fun getAssetSvg(assetPath: String): String {
            if (moduleContext == null) return ""
            return runCatching {
                moduleContext.assets.open(assetPath).bufferedReader().use { it.readText() }
            }.getOrDefault("")
        }

        val singleMobileSVGString = when (Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE.get()) {
            0 -> getAssetSvg(Constants.ASSETS_SVG_SIGNAL_HYPER_OS_SINGLE)
            1 -> getAssetSvg(Constants.ASSETS_SVG_SIGNAL_IOS_SINGLE)
            else -> customSingleSvg?.takeIf { it.isNotBlank() } ?: getAssetSvg(Constants.ASSETS_SVG_SIGNAL_HYPER_OS_SINGLE)
        }

        val stackedMobileSVGString = when (Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED.get()) {
            0 -> getAssetSvg(Constants.ASSETS_SVG_SIGNAL_HYPER_OS_STACKED)
            1 -> getAssetSvg(Constants.ASSETS_SVG_SIGNAL_IOS_STACKED)
            else -> customStackedSvg?.takeIf { it.isNotBlank() } ?: getAssetSvg(Constants.ASSETS_SVG_SIGNAL_HYPER_OS_STACKED)
        }

        singleTypeCenterPercent = StackedMobileIconUtils.extractTypeContainerBounds(singleMobileSVGString)
        stackedTypeCenterPercent = StackedMobileIconUtils.extractTypeContainerBounds(stackedMobileSVGString)
        val alphaFilled = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_FG.get()
        val alphaBackground = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_BG.get()
        val alphaError = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_ERROR.get()
        val done1 = StackedMobileIconUtils.generateSingleSignalPictures(
            singleMobileSVGString = singleMobileSVGString,
            pictureCache = vectorCache,
            alphaFilled = alphaFilled,
            alphaBackground = alphaBackground,
            alphaError = alphaError
        )
        val done2 = StackedMobileIconUtils.generateStackedSignalPictures(
            stackedMobileSVGString = stackedMobileSVGString,
            pictureCache = vectorCache,
            alphaFilled = alphaFilled,
            alphaBackground = alphaBackground,
            alphaError = alphaError,
        )
        return done1 && done2
    }


    fun getIcon(context: Context, state: CellularIconState): Icon? {
        if (!isPreloaded) return null
        ensureEnvironment(context)

        // 1. 尝试从 L3 成品库光速下班
        finalIconCache.get(state)?.let { return it }

        // 2. 没命中？启动流水线合成
        val finalIcon = when (state) {
            is CellularIconState.None -> null
            is CellularIconState.SingleSignal -> renderSingle(state)
            is CellularIconState.StackedSignal -> renderStacked(state)
            is CellularIconState.StandaloneNetType -> {
                renderStandalone(
                    state,
                    context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
                )
            }
        }

        // 3. 产出物存入 L3 并返回
        finalIcon?.let { finalIconCache.put(state, it) }
        return finalIcon
    }

    private fun renderSingle(state: CellularIconState.SingleSignal): Icon? {
        val signalKey = state.level.toString()
        val signalBitmap = getL2SignalBitmap(signalKey) ?: return null
        val finalBitmap = composeSignalAndType(signalBitmap, state.netType, singleTypeCenterPercent)
        return Icon.createWithBitmap(finalBitmap)
    }

    private fun renderStacked(state: CellularIconState.StackedSignal): Icon? {
        val signalKey = "${state.sim1Level}_${state.sim2Level}"
        val signalBitmap = getL2SignalBitmap(signalKey) ?: return null
        val finalBitmap = composeSignalAndType(signalBitmap, state.netType, stackedTypeCenterPercent)
        return Icon.createWithBitmap(finalBitmap)
    }

    private fun renderStandalone(state: CellularIconState.StandaloneNetType, isRtl: Boolean): Icon? {
        if (!isPreloaded) {
            return null
        }

        val type = state.netType

        standaloneTypeIconCache[type]?.let { return it }
        if (currentIconHeightPx <= 0) return null

        val density = currentDensity
        val isCondensed = isStandaloneTypeAutoSpecialOpt && type.length > 2
        val targetTypeface = if (isCondensed) typefaceStandaloneTypeCondensed else typefaceStandaloneTypeNormal
        val targetLetterSpacing = if (isCondensed) 0.02f else 0f // 压缩字体给 2% (0.02f) 的呼吸空间，Normal 保持 0% (0f)
        val isSpecialOpt = isStandaloneTypeAutoSpecialOpt && (type in listOf("4G+", "5G+", "5GA"))

        val baseTextSize = standaloneTypeSizeDp * density
        val subTextSize = baseTextSize * 0.7f

        // 4. 配置抗锯齿文本画笔 (TextPaint)
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE // 必须是纯白，供系统层进行深浅色 Tint
            textAlign = Paint.Align.LEFT
            textSize = baseTextSize
            typeface = targetTypeface
            letterSpacing = targetLetterSpacing
        }

        // 5. 测量排版尺寸
        var mainText = type
        var subText = ""
        var mainWidth = 0.0f
        val textWidth: Float
        var tailSpacePx: Float

        if (isSpecialOpt) {
            // 拆分字符串："4G" / "5G" 作为主体，"+" / "A" 作为角标
            mainText = type.take(2)
            subText = type.substring(2)
            // 测量主文本宽度
            textPaint.textSize = baseTextSize
            mainWidth = textPaint.measureText(mainText)
            // 测量角标文本宽度
            textPaint.textSize = subTextSize
            val subWidth = textPaint.measureText(subText)
            // 总宽度是两者之和
            textWidth = mainWidth + subWidth
            tailSpacePx = subTextSize * targetLetterSpacing
            // 【重要】：测完必须把字体大小恢复到 base，为了后面准确计算统一的 baseline
            textPaint.textSize = baseTextSize
        } else {
            textWidth = textPaint.measureText(type)
            tailSpacePx = baseTextSize * targetLetterSpacing
        }

        val exactVisualTextWidth = textWidth - tailSpacePx
        val paddingStartPx = standaloneTypePaddingStartDp * density
        val paddingEndPx = standaloneTypePaddingEndDp * density
        val actualPaddingLeftPx = if (isRtl) paddingEndPx else paddingStartPx
        val actualPaddingRightPx = if (isRtl) paddingStartPx else paddingEndPx

        // 文本的实际物理高度 (从最高点到底部下沉点)
        val fontMetrics = textPaint.fontMetrics

        // 图标的最终宽高 (向上取整防止边缘被裁切)
        val bitmapWidth = (exactVisualTextWidth + actualPaddingLeftPx + actualPaddingRightPx).toInt()
        val bitmapHeight = currentIconHeightPx

        if (bitmapWidth <= 0 || bitmapHeight <= 0) return null

        // 6. 创建透明底色的 Bitmap
        val bitmap = createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)

        // 7. 计算完美的几何中心与垂直居中基线
        val startX = actualPaddingLeftPx
        val verticalOffsetPx = standaloneTypeVerticalOffsetDp * density
        val centerY = (bitmapHeight / 2f) + verticalOffsetPx
        // Baseline 公式：中心点 Y 减去 (上浮与下沉的平均偏差)
        val baselineY = centerY - (fontMetrics.descent + fontMetrics.ascent) / 2f

        // 8. 绘制文本
        if (isSpecialOpt) {
            // 第一步：用基础字号画主文本
            textPaint.textSize = baseTextSize
            canvas.drawText(mainText, startX, baselineY, textPaint)
            // 第二步：缩小字号，画角标 ("+", "A")
            textPaint.textSize = subTextSize
            // 巧用系统特性：共用同一个 baselineY，所以 "G" 和 "+" 的底部天生平齐！
            // X 坐标为主文本起笔点 + 主文本宽度
            canvas.drawText(subText, startX + mainWidth, baselineY, textPaint)
        } else {
            // 普通场景：直接画完整文本
            canvas.drawText(type, startX, baselineY, textPaint)
        }

        // 9. 包装并缓存
        val icon = Icon.createWithBitmap(bitmap)
        standaloneTypeIconCache[type] = icon

        return icon
    }

    private fun composeSignalAndType(
        signalBitmap: Bitmap,
        netType: String,
        centerPercent: PointF?
    ): Bitmap {
        if (netType.isEmpty() || centerPercent == null) {
            return signalBitmap
        }

        val textBitmap = getL2SmallTypeBitmap(netType) ?: return signalBitmap

        // 1. 计算锚点在信号底图上的物理坐标
        val anchorX = signalBitmap.width * centerPercent.x
        val anchorY = signalBitmap.height * centerPercent.y

        // 2. 计算文本如果以锚点居中，它的上下左右边界在哪里
        // 注意：这里的边界坐标可能是负数（超出了左/上），也可能大于底图宽高（超出了右/下）
        val textLeft = anchorX - (textBitmap.width / 2f)
        val textRight = anchorX + (textBitmap.width / 2f)
        val textTop = anchorY - (textBitmap.height / 2f)

        // 3. 🌟 计算极值，得出最终的超级包围盒 (Bounding Box)
        // 信号底图的边界固定是 (0, 0, width, height)
        val minX = min(0f, textLeft)
        val maxX = max(signalBitmap.width.toFloat(), textRight)

        // 4. 根据极值，推算出新画布的绝对宽高 (向上取整防精度丢失导致边缘一像素被裁)
        val newWidth = ceil(maxX - minX).toInt()
        val newHeight = signalBitmap.height

        // 5. 创建能容纳万物的扩展画布
        val compositeBitmap = createBitmap(newWidth, newHeight, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(compositeBitmap)

        // 6. 🌟 计算画布偏移量 (Canvas Offset)
        // 因为 Canvas 的原点永远是 (0,0)。如果 minX 是 -10，说明文本往左突出了 10px。
        // 我们必须把整个世界往右推 10px，所以 offsetX = -minX (即 +10)
        val offsetX = -minX

        // 7. 画上底图 (加上偏移量)
        canvas.drawBitmap(signalBitmap, offsetX, 0f, null)

        // 8. 画上文本 (文本原本的起笔点 + 偏移量)
        canvas.drawBitmap(textBitmap, textLeft + offsetX, textTop, null)

        return compositeBitmap
    }

    private fun getL2SignalBitmap(key: String): Bitmap? {
        signalBitmapCache[key]?.let { return it }
        val picture = vectorCache[key] ?: return null
        if (currentIconHeightPx <= 0) return null

        val targetHeightPx = currentIconHeightPx
        val targetWidthPx = (targetHeightPx * (picture.width.toFloat() / picture.height.toFloat())).toInt()

        val bitmap = createBitmap(targetWidthPx, targetHeightPx, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        canvas.scale(targetWidthPx.toFloat() / picture.width.toFloat(), targetHeightPx.toFloat() / picture.height.toFloat())
        canvas.drawPicture(picture)

        signalBitmapCache[key] = bitmap
        return bitmap
    }

    private fun getL2SmallTypeBitmap(text: String): Bitmap? {
        smallTypeBitmapCache[text]?.let { return it }

        // 简单的文字绘制，无需外边距，因为我们要通过中心对齐把它嵌进去
        val density = currentDensity
        val textSizePx = smallTypeSizeDp * density
        val isCondensed = isStandaloneTypeAutoSpecialOpt && text.length > 2
        val targetTypeface = if (isCondensed) typefaceSmallTypeCondensed else typefaceSmallTypeNormal
//        val targetLetterSpacing = if (isCondensed) 0.02f else 0f // 压缩字体给 2% (0.02f) 的呼吸空间，Normal 保持 0% (0f)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE // 系统 Tint 必须是白底
            textSize = textSizePx
            typeface = targetTypeface
            textAlign = Paint.Align.LEFT
        }
        val textWidth = textPaint.measureText(text)
        val fontMetrics = textPaint.fontMetrics
        // 文本的实际绝对高度
        val textHeight = fontMetrics.descent - fontMetrics.ascent

        if (textWidth <= 0 || textHeight <= 0) return null

        val bitmap = createBitmap(textWidth.toInt() + 2, textHeight.toInt() + 2, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        // 把文字在自己的极小 Bitmap 里完美居中画出
        val startY = (bitmap.height / 2f) - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
        canvas.drawText(text, 1f, startY, textPaint) // 1f 留出防裁切余量

        smallTypeBitmapCache[text] = bitmap
        return bitmap
    }
}