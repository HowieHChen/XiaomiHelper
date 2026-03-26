package dev.lackluster.mihelper.ui.viewmodel

import android.content.Context
import android.graphics.Picture
import android.graphics.PointF
import android.graphics.Typeface
import android.util.LruCache
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_MOBILE_TYPE_REAL_FILE_NAME
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_REAL_FILE_PATH
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.StackedMobile
import dev.lackluster.mihelper.ui.model.IconDetailPageState
import dev.lackluster.mihelper.ui.model.StackedMobileRenderKey
import dev.lackluster.mihelper.ui.repository.StackedMobileRepository
import dev.lackluster.mihelper.utils.ShellUtils
import dev.lackluster.mihelper.utils.StackedMobileIconUtils
import dev.lackluster.mihelper.utils.SystemProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class StackedMobileIconViewModel : ViewModel() {
    private val vectorCache = LruCache<StackedMobileRenderKey, Map<String, Picture>>(30)
    private val anchorCache = HashMap<Int, PointF?>()
    private val typefaceCache = LruCache<String, Typeface>(60)
    private val validFontPathCache = ConcurrentHashMap<String, String>()

    private var isPreloaded = false

    private val _stackedPictures = MutableStateFlow<Map<String, Picture>>(emptyMap())
    val stackedPictures: StateFlow<Map<String, Picture>> = _stackedPictures.asStateFlow()

    private val _singlePictures = MutableStateFlow<Map<String, Picture>>(emptyMap())
    val singlePictures: StateFlow<Map<String, Picture>> = _singlePictures.asStateFlow()

    val configState = StackedMobileRepository.state

    private val _screenState = MutableStateFlow(IconDetailPageState())
    val screenState = _screenState.asStateFlow()

    fun preload(context: Context) {
        if (isPreloaded) return

        viewModelScope.launch(Dispatchers.Default) {
            val signalSingleMode = SafeSP.getInt(StackedMobile.SIGNAL_SVG_SINGLE, 0)
            val signalSingleSVG = when (signalSingleMode) {
                0 -> Constants.STACKED_MOBILE_ICON_SINGLE_MIUI
                1 -> Constants.STACKED_MOBILE_ICON_SINGLE_IOS
                else -> SafeSP.getString(StackedMobile.SIGNAL_SVG_SINGLE_VAL, Constants.STACKED_MOBILE_ICON_SINGLE_MIUI)
            }
            val signalStackedMode = SafeSP.getInt(StackedMobile.SIGNAL_SVG_STACKED, 0)
            val signalStackedSVG = when (signalStackedMode) {
                0 -> Constants.STACKED_MOBILE_ICON_STACKED_MIUI
                1 -> Constants.STACKED_MOBILE_ICON_STACKED_IOS
                else -> SafeSP.getString(StackedMobile.SIGNAL_SVG_STACKED_VAL, Constants.STACKED_MOBILE_ICON_STACKED_MIUI)
            }
            val alphaFg = SafeSP.getFloat(StackedMobile.SIGNAL_ALPHA_FG, 1.0f)
            val alphaBg = SafeSP.getFloat(StackedMobile.SIGNAL_ALPHA_BG, 0.4f)
            val alphaError = SafeSP.getFloat(StackedMobile.SIGNAL_ALPHA_ERROR, 0.2f)
            if (signalSingleSVG.isNotBlank()) {
                val singleKey = StackedMobileRenderKey(signalSingleSVG.hashCode(), false, alphaFg, alphaBg, alphaError)
                val tempMap = mutableMapOf<String, Picture>()
                if (StackedMobileIconUtils.generateSingleSignalPictures(signalSingleSVG, tempMap, alphaFg, alphaBg, alphaError)) {
                    vectorCache.put(singleKey, tempMap)
                    _singlePictures.value = tempMap
                }
                getAnchor(signalSingleSVG)
            }
            if (signalStackedSVG.isNotBlank()) {
                val stackedKey = StackedMobileRenderKey(signalStackedSVG.hashCode(), true, alphaFg, alphaBg, alphaError)
                val tempMap = mutableMapOf<String, Picture>()
                if (StackedMobileIconUtils.generateStackedSignalPictures(signalStackedSVG, tempMap, alphaFg, alphaBg, alphaError)) {
                    vectorCache.put(stackedKey, tempMap)
                    _stackedPictures.value = tempMap
                }
                getAnchor(signalStackedSVG)
            }

            val fontMode = SafeSP.getInt(StackedMobile.TYPE_FONT_MODE, 0)
            val fontPath = SafeSP.getString(StackedMobile.TYPE_FONT_PATH_INTERNAL, VARIABLE_FONT_DEFAULT_PATH)
            val condensedWidth = SafeSP.getInt(StackedMobile.TYPE_WIDTH_CONDENSED, 80)

            // 信号内小角标的字重
            val smallWeight = SafeSP.getInt(StackedMobile.SMALL_TYPE_FONT_WEIGHT, 630)
            // 独立大图标的字重 (对应源码的 sb_font_stacked_type_single_weight)
            val largeWeight = SafeSP.getInt(StackedMobile.LARGE_TYPE_FONT_WEIGHT, 400)

            // 直接调用我们写好的 getTypeface 方法。
            // 这一步会执行 file.exists() 并进行 Typeface.Builder().build()
            // 因为此时在 Dispatchers.Default 中，完全不卡主线程！

            // A. 预热：信号内部小角标字体
            getTypeface(context, fontMode, fontPath, smallWeight, condensedWidth, false)

            // B. 预热：独立网络类型大图标字体 (常规版)
            getTypeface(context, fontMode, fontPath, largeWeight, condensedWidth, false)

            // C. 预热：独立网络类型大图标字体 (压缩版，如果是 5GA 这种会自动用到)
            if (fontMode == 2 || fontMode == 3) {
                getTypeface(context, fontMode, fontPath, largeWeight, condensedWidth, true)
            }
            isPreloaded = true
        }
    }

    fun updateStackedSvg(svg: String, alphaFg: Float, alphaBg: Float, alphaError: Float) {
        if (svg.isBlank()) return
        val key = StackedMobileRenderKey(svg.hashCode(), true, alphaFg, alphaBg, alphaError)

        val cached = vectorCache.get(key)
        if (cached != null) {
            _stackedPictures.value = cached
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val tempMap = mutableMapOf<String, Picture>()
            val success = StackedMobileIconUtils.generateStackedSignalPictures(
                svg, tempMap, alphaFg, alphaBg, alphaError
            )
            if (success) {
                vectorCache.put(key, tempMap)
                _stackedPictures.value = tempMap
            }
        }
    }

    fun updateSingleSvg(svg: String, alphaFg: Float, alphaBg: Float, alphaError: Float) {
        if (svg.isBlank()) return
        val key = StackedMobileRenderKey(svg.hashCode(), false, alphaFg, alphaBg, alphaError)

        val cached = vectorCache.get(key)
        if (cached != null) {
            _singlePictures.value = cached
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val tempMap = mutableMapOf<String, Picture>()
            val success = StackedMobileIconUtils.generateSingleSignalPictures(
                svg, tempMap, alphaFg, alphaBg, alphaError
            )
            if (success) {
                vectorCache.put(key, tempMap)
                _singlePictures.value = tempMap
            }
        }
    }

    fun getAnchor(svg: String): PointF? {
        if (svg.isBlank()) return null
        val hash = svg.hashCode()
        if (!anchorCache.containsKey(hash)) {
            val anchor = StackedMobileIconUtils.extractTypeContainerBounds(svg)
            anchorCache[hash] = anchor
        }
        return anchorCache[hash]
    }

    fun getTypeface(
        context: Context,
        mode: Int,
        path: String?,
        weight: Int,
        condensedWidth: Int,
        isCondensed: Boolean = false
    ): Typeface {
        if (mode == 0) {
            return Typeface.DEFAULT_BOLD
        }

        val customFont = (mode == 1)
        val resolvedPath = if (customFont) {
            val pathConfigKey = path ?: "empty_path"
            validFontPathCache.getOrPut(pathConfigKey) {
                val defaultPath = SystemProperties.get("ro.miui.ui.font.mi_font_path", VARIABLE_FONT_DEFAULT_PATH)
                if (!path.isNullOrBlank()) {
                    val fontFile = File(path)
                    if (fontFile.exists() && fontFile.isFile && fontFile.canRead()) path else defaultPath
                } else {
                    defaultPath
                }
            }
        } else {
            // mode 2: MiSans, mode 3: SFPro
            if (mode == 2) "fonts/MiSansCondensed-Subset.ttf" else "fonts/SFPro-Subset.ttf"
        }

        val appliedWidth = if (customFont) -1 else if (isCondensed) condensedWidth else 100
        val typefaceKey = "${mode}_${resolvedPath}_${weight}_${appliedWidth}"

        var cachedTypeface = typefaceCache.get(typefaceKey)

        if (cachedTypeface == null) {
            try {
                cachedTypeface = if (customFont) {
                    Typeface.Builder(resolvedPath).apply {
                        setFontVariationSettings("'wght' $weight")
                    }.build()
                } else {
                    Typeface.Builder(context.assets, resolvedPath).apply {
                        setFontVariationSettings("'wght' $weight, 'wdth' $appliedWidth")
                    }.build()
                }
                typefaceCache.put(typefaceKey, cachedTypeface)
                return cachedTypeface
            } catch (_: Throwable) {
                return Typeface.DEFAULT_BOLD
            }
        } else {
            return cachedTypeface
        }
    }

    override fun onCleared() {
        super.onCleared()
        vectorCache.evictAll()
        typefaceCache.evictAll()
    }

    fun dismissErrorDialog() {
        _screenState.update { it.copy(errorDialogMessage = null) }
    }

    fun updatePreference(key: String, value: Any) {
        StackedMobileRepository.updatePreference(key, value)
    }

    fun updateFontPath(newPath: String, defaultPath: String, errorHint: String) {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            val result = withContext(Dispatchers.IO) {
                handleFontCopyInternal(newPath, defaultPath, errorHint)
            }
            _screenState.update {
                it.copy(
                    isLoading = false,
                    errorDialogMessage = result
                )
            }
        }
    }

    private fun handleFontCopyInternal(newPath: String, defaultPath: String, errorHint: String): String? {
        if (newPath == defaultPath) {
            StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_REAL, defaultPath)
            StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_INTERNAL, defaultPath)
            return null
        }

        val file = File(newPath)
        if (!file.exists() || !file.isFile) return null

        val oriFilePath = file.absolutePath
        val newFilePath = "${VARIABLE_FONT_REAL_FILE_PATH}/${VARIABLE_FONT_MOBILE_TYPE_REAL_FILE_NAME}"

        return try {
            ShellUtils.tryExec("cp -f $oriFilePath $newFilePath", useRoot = true, throwIfError = true)
            ShellUtils.tryExec("chmod 755 $newFilePath", useRoot = true, throwIfError = true)

            StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_REAL, oriFilePath)
            StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_INTERNAL, newFilePath)
            null
        } catch (t: Throwable) {
            YLog.error("error", t)
            if (t.message?.trim()?.endsWith("Permission denied") == true) {
                StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_REAL, oriFilePath)
                StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_INTERNAL, oriFilePath)
                errorHint
            } else {
                StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_REAL, defaultPath)
                StackedMobileRepository.updatePreference(StackedMobile.TYPE_FONT_PATH_INTERNAL, defaultPath)
                "复制文件失败，已重置为默认字体"
            }
        }
    }

    fun validateAndUpdateSingleSvg(svgContent: String) {
        validateAndUpdateSignalSVG(
            svgContent = svgContent,
            requiredIds = listOf(
                "signal_1", "signal_2", "signal_3", "signal_4",
            ),
            key = StackedMobile.SIGNAL_SVG_SINGLE_VAL
        )
    }

    fun validateAndUpdateStackedSvg(svgContent: String) {
        validateAndUpdateSignalSVG(
            svgContent = svgContent,
            requiredIds = listOf(
                "signal_1_1", "signal_1_2", "signal_1_3", "signal_1_4",
                "signal_2_1", "signal_2_2", "signal_2_3", "signal_2_4"
            ),
            key = StackedMobile.SIGNAL_SVG_STACKED_VAL
        )
    }

    private fun validateAndUpdateSignalSVG(svgContent: String, requiredIds: List<String>, key: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _screenState.update { it.copy(isLoading = true) }
            if (svgContent.isBlank()) {
                _screenState.update {
                    it.copy(isLoading = false, errorDialogMessage = "SVG 不能为空")
                }
                return@launch
            }
            val missingId = requiredIds.find { id ->
                val regex = Regex("""id\s*=\s*['"]$id['"]""")
                !regex.containsMatchIn(svgContent)
            }
            if (missingId != null) {
                _screenState.update {
                    it.copy(
                        isLoading = false,
                        errorDialogMessage = "SVG Validation failed: Missing required ID -> $missingId"
                    )
                }
            } else {
                StackedMobileRepository.updatePreference(key, svgContent)
                _screenState.update {
                    it.copy(isLoading = false, errorDialogMessage = null)
                }
            }
        }
    }
}