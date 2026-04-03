package dev.lackluster.mihelper.app.repository

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.SystemProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

enum class FontTarget(
    val spKey: PreferenceKey<String>,
    val targetFileName: String
) {
    STATUS_BAR(
        spKey = Preferences.SystemUI.StatusBar.Font.FONT_PATH_INTERNAL,
        targetFileName = Constants.VARIABLE_FONT_REAL_FILE_NAME
    ),

    STACKED_TYPE(
        spKey = Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_INTERNAL,
        targetFileName = Constants.VARIABLE_FONT_MOBILE_TYPE_REAL_FILE_NAME
    )
}

enum class FontMode {
    DEFAULT,
    MI_SANS,
    MI_SANS_CONDENSED,
    SF_PRO,
    FROM_FILE,
}

private data class FontCacheKey(
    val target: FontTarget,
    val mode: FontMode,
    val weight: Int,
    val appliedWidth: Int
)

class FontRepository(
    private val context: Context,
    private val repo: GlobalPreferencesRepository
) {
    private val fontCache = ConcurrentHashMap<FontCacheKey, Pair<Typeface, FontFamily>>()

    private val _fontUpdateEvent = MutableSharedFlow<FontTarget>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val fontUpdateEvent = _fontUpdateEvent.asSharedFlow()

    private val vfDefaultPath by lazy {
        SystemProperties.get("ro.miui.ui.font.mi_font_path", Constants.VARIABLE_FONT_DEFAULT_PATH)
    }

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            repo.globalReloadEvent.collect {
                fontCache.clear()
                FontTarget.entries.forEach { target ->
                    _fontUpdateEvent.emit(target)
                }
            }
        }
    }

    private fun getOrCreateFont(
        target: FontTarget,
        weight: Int,
        mode: FontMode,
        condensedWidth: Int,
        isCondensed: Boolean
    ): Pair<Typeface, FontFamily> {
        val appliedWidth = if (isCondensed) condensedWidth else 100
        val key = FontCacheKey(target, mode, weight, appliedWidth)

        return fontCache.getOrPut(key) {
            val typeface = try {
                when (mode) {
                    FontMode.DEFAULT -> Typeface.DEFAULT_BOLD
                    FontMode.MI_SANS -> {
                        Typeface.Builder(vfDefaultPath)
                            .setFontVariationSettings("'wght' $weight, 'wdth' $appliedWidth")
                            .build()
                    }
                    FontMode.FROM_FILE -> {
                        val currentPath = repo.get(target.spKey)
                        val fontFile = File(currentPath)
                        val resolvedPath = if (fontFile.exists() && fontFile.isFile && fontFile.canRead()) currentPath else vfDefaultPath
                        Typeface.Builder(resolvedPath)
                            .setFontVariationSettings("'wght' $weight")
                            .build() ?: Typeface.DEFAULT_BOLD
                    }
                    FontMode.MI_SANS_CONDENSED, FontMode.SF_PRO -> {
                        val resolvedPath =
                            if (mode == FontMode.MI_SANS_CONDENSED) Constants.VARIABLE_FONT_MI_SANS_CONDENSED_PATH
                            else Constants.VARIABLE_FONT_SF_PRO_PATH
                        Typeface.Builder(context.assets, resolvedPath)
                            .setFontVariationSettings("'wght' $weight, 'wdth' $appliedWidth")
                            .build() ?: Typeface.DEFAULT_BOLD
                    }
                }
            } catch (_: Exception) {
                Typeface.DEFAULT_BOLD
            }
            Pair(typeface, FontFamily(typeface))
        }
    }

    fun getNativeTypeface(
        target: FontTarget,
        weight: Int,
        mode: FontMode = FontMode.DEFAULT,
        condensedWidth: Int = 100,
        isCondensed: Boolean = false
    ): Typeface {
        return getOrCreateFont(target, weight, mode, condensedWidth, isCondensed).first
    }

    fun getFontFamily(
        target: FontTarget,
        weight: Int,
        mode: FontMode = FontMode.DEFAULT,
        condensedWidth: Int = 100,
        isCondensed: Boolean = false
    ): FontFamily {
        return getOrCreateFont(target, weight, mode, condensedWidth, isCondensed).second
    }

    suspend fun applyCustomFont(tempFilePath: String, target: FontTarget): Boolean = withContext(Dispatchers.IO) {
        try {
            if (tempFilePath == vfDefaultPath) {
                resetToDefault(target)
                return@withContext true
            }

            val newFilePath = "${Constants.VARIABLE_FONT_REAL_FILE_PATH}/${target.targetFileName}"

            SystemCommander.execAsync(
                command = "cp -f $tempFilePath $newFilePath",
                useRoot = true,
                silent = false
            )
            SystemCommander.execAsync(
                command = "chmod 755 $newFilePath",
                useRoot = true,
                silent = false
            )

            repo.update(target.spKey, newFilePath)
            fontCache.keys.removeIf { it.target == target }
            _fontUpdateEvent.emit(target)

            return@withContext true
        } catch (_: Exception) {
            return@withContext false
        }
    }

    suspend fun resetToDefault(target: FontTarget) = withContext(Dispatchers.IO) {
        repo.update(target.spKey, Constants.VARIABLE_FONT_DEFAULT_PATH)
        fontCache.keys.removeIf { it.target == target }
        _fontUpdateEvent.emit(target)
    }
}