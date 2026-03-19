package dev.lackluster.mihelper.hook.rules.systemui.mobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.text.TextPaint
import android.view.View
import androidx.core.graphics.createBitmap
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.FontWeight
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.StackedMobileIconUtils
import dev.lackluster.mihelper.utils.SystemProperties
import java.io.File
import kotlin.collections.set

object StackedMobileIconCache {
    private const val ICON_HEIGHT_DP = 20

    private val valueTypeSize = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_SIZE, 14.0f)
    private val valuePaddingStart = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_PADDING_START_VAL, 2.0f)
    private val valuePaddingEnd = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_PADDING_END_VAL, 2.0f)
    private val valueVerticalOffset = Prefs.getFloat(IconTuner.STACKED_MOBILE_TYPE_VERTICAL_OFFSET, 0.0f)
    private val valueTypeFW = Prefs.getInt(FontWeight.STACKED_MOBILE_TYPE_VAL, 400).coerceIn(1..1000)
    private val valueTypeFont = Prefs.getInt(FontWeight.STACKED_MOBILE_TYPE_FONT, 0)
    private val valueTypeWidthCondensed = Prefs.getInt(FontWeight.STACKED_MOBILE_TYPE_WIDTH_CONDENSED, 80).coerceIn(10, 200)
    private var typefaceTypeNormal = Typeface.DEFAULT_BOLD
    private var typefaceTypeCondensed = Typeface.DEFAULT_BOLD
    private var typeAutoOpt = (valueTypeFont == 2 || valueTypeFont == 3)

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

    fun preload(context: Context): Boolean {
        if (isPreloaded) return false
        synchronized(this) {
            if (isPreloaded) return false
            var fallbackToDefaultFont = false
            if (valueTypeFont == 1) {
                val defaultPath = SystemProperties.get("ro.miui.ui.font.mi_font_path", VARIABLE_FONT_DEFAULT_PATH)
                val prefPath = Prefs.getString(FontWeight.STACKED_MOBILE_TYPE_FONT_PATH_INTERNAL, defaultPath)
                val fontFile = File(prefPath)
                val finalPath = if (fontFile.exists() && fontFile.isFile && fontFile.canRead()) prefPath else defaultPath
                typefaceTypeNormal = Typeface.Builder(finalPath).setFontVariationSettings("'wght' $valueTypeFW").build()
            } else if (valueTypeFont != 0) {
                try {
                    val moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
                    val fontFileName = if (valueTypeFont == 2) "fonts/MiSansCondensed-Subset.ttf" else "fonts/SFPro-Subset.ttf"
                    typefaceTypeNormal = Typeface.Builder(moduleContext.assets, fontFileName)
                        .setFontVariationSettings("'wght' $valueTypeFW, 'wdth' 100")
                        .build()
                    typefaceTypeCondensed = Typeface.Builder(moduleContext.assets, fontFileName)
                        .setFontVariationSettings("'wght' $valueTypeFW, 'wdth' $valueTypeWidthCondensed")
                        .build()
                } catch (t: Throwable) {
                    fallbackToDefaultFont = true
                    YLog.warn(t)
                }
            }
            if (fallbackToDefaultFont) {
                typefaceTypeNormal = Typeface.DEFAULT_BOLD
            }
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
            "4G,4G+,LTE,5G,5G+,5GA".split(",").forEach {
                getTypeIcon(context, it)
            }
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

        val bitmap = createBitmap(targetWidthPx, targetHeightPx, Bitmap.Config.ALPHA_8)
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

        val isCondensed = typeAutoOpt && type.length > 2
        val targetTypeface = if (isCondensed) typefaceTypeCondensed else typefaceTypeNormal
        val targetLetterSpacing = if (isCondensed) 0.02f else 0f // 压缩字体给 2% (0.02f) 的呼吸空间，Normal 保持 0% (0f)
        val isSpecialOpt = typeAutoOpt && (type in listOf("4G+", "5G+", "5GA"))

        val baseTextSize = valueTypeSize * density
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
        val paddingStartPx = valuePaddingStart * density
        val paddingEndPx = valuePaddingEnd * density
        val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        val actualPaddingLeftPx = if (isRtl) paddingEndPx else paddingStartPx
        val actualPaddingRightPx = if (isRtl) paddingStartPx else paddingEndPx

        // 文本的实际物理高度 (从最高点到底部下沉点)
        val fontMetrics = textPaint.fontMetrics

        // 图标的最终宽高 (向上取整防止边缘被裁切)
        val bitmapWidth = (exactVisualTextWidth + actualPaddingLeftPx + actualPaddingRightPx).toInt()
        val bitmapHeight = (ICON_HEIGHT_DP * density).toInt()

        if (bitmapWidth <= 0 || bitmapHeight <= 0) return null

        // 6. 创建透明底色的 Bitmap
        val bitmap = createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)

        // 7. 计算完美的几何中心与垂直居中基线
        val startX = actualPaddingLeftPx
        val verticalOffsetPx = valueVerticalOffset * density
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
        typeIconCache[type] = icon

        return icon
    }
}