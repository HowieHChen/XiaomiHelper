package dev.lackluster.mihelper.app.repository

import android.graphics.Picture
import android.graphics.PointF
import android.graphics.Typeface
import android.util.LruCache
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.LargeTypeState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.SignalIconState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.SmallTypeState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.StackedMobileState
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.TypefaceState
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.StackedMobileIconUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_VAL,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED,
    Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_VAL,
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

class StackedMobileRepository(
    private val font: FontRepository,
    private val repo: GlobalPreferencesRepository,
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val vectorCache = LruCache<StackedMobileRenderKey, Map<String, Picture>>(30)
    private val anchorCache = ConcurrentHashMap<Int, AnchorWrapper>()

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
            repo.preferenceUpdates.collect { updatedKey ->
                if (relevantKeys.contains(updatedKey)) {
                    _configState.update { loadInitialConfig() }
                }
            }
        }
        repositoryScope.launch(Dispatchers.Default) {
            repo.globalReloadEvent.collect {
                _configState.update { loadInitialConfig() }
            }
        }
        repositoryScope.launch(Dispatchers.Default) {
            _configState.map { it.signal }
                .distinctUntilChanged()
                .collect { signalConfig ->
                    updateSingleSvg(
                        svg = signalConfig.effectiveSingleSVG,
                        alphaFg = signalConfig.alphaFg,
                        alphaBg = signalConfig.alphaBg,
                        alphaError = signalConfig.alphaError
                    )
                    updateStackedSvg(
                        svg = signalConfig.effectiveStackedSVG,
                        alphaFg = signalConfig.alphaFg,
                        alphaBg = signalConfig.alphaBg,
                        alphaError = signalConfig.alphaError
                    )
                }
        }
        repositoryScope.launch(Dispatchers.Default) {
            val fontMode = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE)

            val mode = when (fontMode) {
                1 -> FontMode.FROM_FILE
                2 -> FontMode.MI_SANS_CONDENSED
                3 -> FontMode.SF_PRO
                else -> FontMode.DEFAULT
            }
            font.getNativeTypeface(
                target = FontTarget.STACKED_TYPE,
                mode = mode,
            )
        }
    }

    private fun loadInitialConfig(): StackedMobileState {
        return StackedMobileState(
            enabled = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.ENABLED),
            signal = SignalIconState(
                singleStyle = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE),
                singleSVG = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_VAL),
                singleSVGName = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_SINGLE_NAME),
                stackedStyle = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED),
                stackedSVG = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_VAL),
                stackedSVGName = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_SVG_STACKED_NAME),
                alphaFg = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_FG),
                alphaBg = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_BG),
                alphaError = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SIGNAL_ALPHA_ERROR),
            ),
            font = TypefaceState(
                mode = parseFontMode(repo.get(Preferences.SystemUI.StatusBar.StackedMobile.TYPE_FONT_MODE)),
                displayName = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_ORIGINAL),
                condensedWidth = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.TYPE_WIDTH_CONDENSED),
            ),
            small = SmallTypeState(
                size = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SIZE),
                weight = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_FONT_WEIGHT),
                showOnStacked = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_STACKED),
                showOnSingle = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ON_SINGLE),
                showRoaming = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.SMALL_TYPE_SHOW_ROAMING),
            ),
            large = LargeTypeState(
                size = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_SIZE),
                weight = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_FONT_WEIGHT),
                paddingStart = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_START_VAL),
                paddingEnd = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_PADDING_END_VAL),
                verticalOffset = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_VERTICAL_OFFSET),
                hideWhenWifi = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_WIFI),
                hideWhenDisconnect = repo.get(Preferences.SystemUI.StatusBar.StackedMobile.LARGE_TYPE_HIDE_WHEN_DISCONNECT),
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
        return font.getNativeTypeface(
            mode = mode,
            target = FontTarget.STACKED_TYPE,
        )
    }

    suspend fun validateAndUpdateSignalSVG(
        svgContent: String,
        svgName: String,
        requiredIds: List<String>,
        contentKey: PreferenceKey<String>,
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
            repo.update(contentKey, compressedSvg)
            repo.update(nameKey, svgName)
            Result.success(Unit)
        }
    }
}