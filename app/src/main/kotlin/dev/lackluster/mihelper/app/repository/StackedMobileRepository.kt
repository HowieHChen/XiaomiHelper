package dev.lackluster.mihelper.app.repository

import android.content.Context
import android.graphics.Picture
import android.graphics.PointF
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import android.util.LruCache
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.LargeTypeState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.SignalIconState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.SmallTypeState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.StackedMobileState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.TypefaceState
import dev.lackluster.mihelper.app.utils.RemoteFileStore
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.MLog
import dev.lackluster.mihelper.utils.StackedMobileIconUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

data class StackedMobileRenderKey(
    val svgHash: Int,
    val isStacked: Boolean,
    val alphaFg: Float,
    val alphaBg: Float,
    val alphaError: Float,
)

class AnchorWrapper(val point: PointF?)

private val relevantKeys: Set<PreferenceKey<*>> = setOf(
    Preferences.SystemUI.StatusBar.StackedMobile.ENABLED,

    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE,
//    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_VAL,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED,
//    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_VAL,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_NAME,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_FG,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_BG,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_ERROR,

    Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE,
    Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_DISPLAY,
    Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_ORIGINAL,
    Preferences.SystemUI.StatusBar.StackedMobile.TYPE_WIDTH_CONDENSED,

    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT,
    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI,
    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_SIZE,
    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_FONT_WEIGHT,
    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_START_VAL,
    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_END_VAL,
    Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_VERTICAL_OFFSET,

    Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_STACKED,
    Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE,
    Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ROAMING,
    Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SIZE,
    Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_FONT_WEIGHT,
)

private const val TAG = "StackedMobileRepository"

class StackedMobileRepository(
    private val context: Context,
    private val fontRepo: FontRepository,
    private val prefRepo: GlobalPreferencesRepository,
    private val fileStore: RemoteFileStore
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val vectorCache = LruCache<StackedMobileRenderKey, Map<String, Picture>>(30)
    private val anchorCache = ConcurrentHashMap<Int, AnchorWrapper>()
    private val assetSvgCache = ConcurrentHashMap<String, String>()

    private val _configState = MutableStateFlow(loadInitialConfig())
    val configState = _configState.asStateFlow()

    private val _stackedPictures = MutableStateFlow<Map<String, Picture>>(emptyMap())
    val stackedPictures = _stackedPictures.asStateFlow()

    private val _singlePictures = MutableStateFlow<Map<String, Picture>>(emptyMap())
    val singlePictures = _singlePictures.asStateFlow()

    private val _stackedAnchor = MutableStateFlow<PointF?>(null)
    val stackedAnchor = _stackedAnchor.asStateFlow()

    private val _singleAnchor = MutableStateFlow<PointF?>(null)
    val singleAnchor = _singleAnchor.asStateFlow()

    init {
        repositoryScope.launch(Dispatchers.Default) {
            prefRepo.preferenceUpdates.collect { updatedKey ->
                if (relevantKeys.contains(updatedKey)) {
                    _configState.update { current ->
                        loadInitialConfig(current)
                    }
                }
            }
        }
        repositoryScope.launch(Dispatchers.Default) {
            prefRepo.globalReloadEvent.collect {
                _configState.update { current ->
                    loadInitialConfig(current)
                }
            }
        }
        repositoryScope.launch(Dispatchers.Default) {
            combine(
                fileStore.isReady,
                _configState.map { it.signal.singleStyle }.distinctUntilChanged(),
                _configState.map { it.signal.stackedStyle }.distinctUntilChanged()
            ) { ready, singleStyle, stackedStyle ->
                ready && (singleStyle == 2 || stackedStyle == 2)
            }.collect { needRemote ->
                if (needRemote) {
                    reloadRemoteSvgs()
                }
            }
        }
        repositoryScope.launch(Dispatchers.Default) {
            _configState.map { it.signal }
                .distinctUntilChanged()
                .collect { signalConfig ->
                    val finalSingleSvg = resolveSingleSvg(signalConfig.singleStyle, signalConfig.singleSVG)
                    val finalStackedSvg = resolveStackedSvg(signalConfig.stackedStyle, signalConfig.stackedSVG)

                    updateSingleSvg(
                        svg = finalSingleSvg,
                        alphaFg = signalConfig.alphaFg,
                        alphaBg = signalConfig.alphaBg,
                        alphaError = signalConfig.alphaError
                    )
                    updateStackedSvg(
                        svg = finalStackedSvg,
                        alphaFg = signalConfig.alphaFg,
                        alphaBg = signalConfig.alphaBg,
                        alphaError = signalConfig.alphaError
                    )
                }
        }
        repositoryScope.launch(Dispatchers.Default) {
            val fontMode = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE)

            val mode = when (fontMode) {
                1 -> FontMode.FROM_FILE
                2 -> FontMode.MI_SANS_CONDENSED
                3 -> FontMode.SF_PRO
                else -> FontMode.DEFAULT
            }
            fontRepo.getNativeTypeface(
                target = FontTarget.STACKED_TYPE,
                mode = mode,
            )
        }
    }

    private fun loadInitialConfig(currentState: StackedMobileState? = null): StackedMobileState {
        return StackedMobileState(
            enabled = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.ENABLED),
            signal = SignalIconState(
                singleStyle = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE),
                singleSVG = currentState?.signal?.singleSVG ?: "",
                singleSVGName = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME),
                stackedStyle = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED),
                stackedSVG = currentState?.signal?.stackedSVG ?: "",
                stackedSVGName = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_NAME),
                alphaFg = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_FG),
                alphaBg = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_BG),
                alphaError = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_ERROR),
            ),
            font = TypefaceState(
                mode = parseFontMode(prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE)),
                displayName = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_ORIGINAL),
                condensedWidth = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.TYPE_WIDTH_CONDENSED),
            ),
            small = SmallTypeState(
                size = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SIZE),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_FONT_WEIGHT),
                showOnStacked = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_STACKED),
                showOnSingle = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE),
                showRoaming = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ROAMING),
            ),
            large = LargeTypeState(
                size = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_SIZE),
                weight = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_FONT_WEIGHT),
                paddingStart = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_START_VAL),
                paddingEnd = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_END_VAL),
                verticalOffset = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_VERTICAL_OFFSET),
                hideWhenWifi = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI),
                hideWhenDisconnect = prefRepo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT),
            )
        )
    }

    private fun parseFontMode(mode: Int): FontMode {
        return when (mode) {
            1 -> FontMode.FROM_FILE
            2 -> FontMode.MI_SANS_CONDENSED
            3 -> FontMode.SF_PRO
            else -> FontMode.DEFAULT
        }
    }

    private suspend fun reloadRemoteSvgs() {
        val singleSvgContent = fileStore.readText(Constants.REMOTE_FILE_STACKED_SIGNAL_SINGLE)
        val stackedSvgContent = fileStore.readText(Constants.REMOTE_FILE_STACKED_SIGNAL_STACKED)

        _configState.update { state ->
            state.copy(
                signal = state.signal.copy(
                    singleSVG = singleSvgContent ?: "",
                    stackedSVG = stackedSvgContent ?: ""
                )
            )
        }
    }

    private fun getSvgFromAssets(assetPath: String): String {
        return assetSvgCache.getOrPut(assetPath) {
            runCatching {
                context.assets.open(assetPath).bufferedReader().use { it.readText() }
            }.onFailure {
                MLog.e(TAG, it) { "读取内置 SVG 失败: $assetPath" }
            }.getOrDefault("")
        }
    }

    private fun resolveSingleSvg(style: Int, customSvg: String): String {
        val resolvedStyle = if (style !in 0..1 && customSvg.isBlank()) 0 else style
        return when (resolvedStyle) {
            0 -> getSvgFromAssets(Constants.ASSETS_SVG_SIGNAL_HYPER_OS_SINGLE)
            1 -> getSvgFromAssets(Constants.ASSETS_SVG_SIGNAL_IOS_SINGLE)
            else -> customSvg // style == 2 时使用自定义的 SVG (从 RemoteFile 读出来的那个)
        }
    }

    private fun resolveStackedSvg(style: Int, customSvg: String): String {
        val resolvedStyle = if (style !in 0..1 && customSvg.isBlank()) 0 else style
        return when (resolvedStyle) {
            0 -> getSvgFromAssets(Constants.ASSETS_SVG_SIGNAL_HYPER_OS_STACKED)
            1 -> getSvgFromAssets(Constants.ASSETS_SVG_SIGNAL_IOS_STACKED)
            else -> customSvg
        }
    }

    fun updateSingleSvg(svg: String, alphaFg: Float, alphaBg: Float, alphaError: Float) {
        if (svg.isBlank()) return
        val key = StackedMobileRenderKey(svg.hashCode(), false, alphaFg, alphaBg, alphaError)

        val cached = vectorCache.get(key)
        if (cached != null) {
            _singlePictures.value = cached
            _singleAnchor.value = getAnchor(svg)
            return
        }

        repositoryScope.launch(Dispatchers.Default) {
            val tempMap = mutableMapOf<String, Picture>()
            val success = StackedMobileIconUtils.generateSingleSignalPictures(
                svg, tempMap, alphaFg, alphaBg, alphaError
            )
            if (success) {
                _singleAnchor.value = getAnchor(svg)
                vectorCache.put(key, tempMap)
                _singlePictures.value = tempMap
            }
        }
    }

    fun updateStackedSvg(svg: String, alphaFg: Float, alphaBg: Float, alphaError: Float) {
        if (svg.isBlank()) return
        val key = StackedMobileRenderKey(svg.hashCode(), true, alphaFg, alphaBg, alphaError)

        val cached = vectorCache.get(key)
        if (cached != null) {
            _stackedPictures.value = cached
            _stackedAnchor.value = getAnchor(svg)
            return
        }

        repositoryScope.launch(Dispatchers.Default) {
            val tempMap = mutableMapOf<String, Picture>()
            val success = StackedMobileIconUtils.generateStackedSignalPictures(
                svg, tempMap, alphaFg, alphaBg, alphaError
            )
            if (success) {
                _stackedAnchor.value = getAnchor(svg)
                vectorCache.put(key, tempMap)
                _stackedPictures.value = tempMap
            }
        }
    }

    private fun getAnchor(svg: String): PointF? {
        if (svg.isBlank()) return null
        val hash = svg.hashCode()
        return anchorCache.computeIfAbsent(hash) {
            val bounds = StackedMobileIconUtils.extractTypeContainerBounds(svg)
            AnchorWrapper(bounds)
        }.point
    }

    fun getTypeface(
        mode: FontMode,
    ): Typeface {
        return fontRepo.getNativeTypeface(
            mode = mode,
            target = FontTarget.STACKED_TYPE,
        )
    }

    suspend fun importSvgFromUri(uri: Uri, isStacked: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val fileName = getFileNameFromUri(uri)

            if (!fileName.lowercase().endsWith(".svg")) {
                return@withContext Result.failure(Exception("Invalid file type: extension is not .svg"))
            }

            val svgContent = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            }

            if (svgContent.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Empty file"))
            }

            if (isStacked) {
                return@withContext validateAndUpdateSignalSVG(
                    svgContent = svgContent,
                    svgName = fileName,
                    requiredIds = listOf("signal_1_1", "signal_1_2", "signal_1_3", "signal_1_4", "signal_2_1", "signal_2_2", "signal_2_3", "signal_2_4"),
                    targetFileName = Constants.REMOTE_FILE_STACKED_SIGNAL_STACKED,
                    nameKey = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_NAME,
                )
            } else {
                return@withContext validateAndUpdateSignalSVG(
                    svgContent = svgContent,
                    svgName = fileName,
                    requiredIds = listOf("signal_1", "signal_2", "signal_3", "signal_4"),
                    targetFileName = Constants.REMOTE_FILE_STACKED_SIGNAL_SINGLE,
                    nameKey = Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME,
                )
            }
        } catch (e: Exception) {
            MLog.e(TAG, e) { "读取 SVG 文件异常" }
            return@withContext Result.failure(Exception("读取文件失败: ${e.message}"))
        }
    }

    private suspend fun validateAndUpdateSignalSVG(
        svgContent: String,
        svgName: String,
        requiredIds: List<String>,
        targetFileName: String,
        nameKey: PreferenceKey<String>
    ): Result<Unit> = withContext(Dispatchers.Default) {
        if (svgContent.isBlank()) {
            return@withContext Result.failure(Exception("SVG 不能为空"))
        }
        val compressedSvg = svgContent.trim()
            .replace(Regex("\\s+"), " ")
            .replace(Regex(">\\s+<"), "><")

        val missingId = requiredIds.find { id ->
            val regex = Regex("""id\s*=\s*['"]$id['"]""")
            !regex.containsMatchIn(compressedSvg)
        }

        if (missingId != null) {
            Result.failure(Exception("SVG Validation failed: Missing required ID -> $missingId"))
        } else {
            val success = fileStore.writeText(targetFileName, compressedSvg)
            if (success) {
                prefRepo.update(nameKey, svgName)
                reloadRemoteSvgs()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to write remote file: $targetFileName"))
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                } catch (t: Throwable) {
                    MLog.e(t, TAG)
                }
            }
        }
        if (result == null) {
            result = uri.path?.let { File(it).name }
        }
        return result ?: "UNKNOWN"
    }
}