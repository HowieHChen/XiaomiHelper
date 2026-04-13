package dev.lackluster.mihelper.app.repository

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.utils.RemoteFileStore
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.MLog
import dev.lackluster.mihelper.utils.SystemProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

enum class FontTarget(
    val displayNameSpKey: PreferenceKey<String>,
    val originalPathSpKey: PreferenceKey<String>,
    val targetFileName: String
) {
    STATUS_BAR(
        displayNameSpKey = Preferences.SystemUI.StatusBar.Font.FONT_PATH_DISPLAY,
        originalPathSpKey = Preferences.SystemUI.StatusBar.Font.FONT_PATH_ORIGINAL,
        targetFileName = Constants.REMOTE_FILE_STATUS_BAR_FONT
    ),

    STACKED_TYPE(
        displayNameSpKey = Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_DISPLAY,
        originalPathSpKey = Preferences.SystemUI.StatusBar.StackedMobile.FONT_PATH_ORIGINAL,
        targetFileName = Constants.REMOTE_FILE_STACKED_MOBILE_TYPE_FONT
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
)

private const val TAG = "FontRepository"

class FontRepository(
    private val context: Context,
    private val prefRepo: GlobalPreferencesRepository,
    private val fileStore: RemoteFileStore
) {
    private val fontCache = ConcurrentHashMap<FontCacheKey, Typeface>()

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
            merge(
                prefRepo.globalReloadEvent,
                fileStore.isReady
            ).collect { trigger ->
                if (trigger is Unit) {
                    fontCache.keys.removeIf { it.mode != FontMode.FROM_FILE }
                }
                val isReady = fileStore.isReady.value
                if (isReady) {
                    FontTarget.entries.forEach { target ->
                        val prefPath = prefRepo.get(target.originalPathSpKey)
                        if (prefPath != target.originalPathSpKey.default) {
                            reloadRemoteFontToCache(target)
                        } else {
                            val key = FontCacheKey(target, FontMode.FROM_FILE)
                            if (fontCache.containsKey(key)) {
                                fontCache.remove(key)
                                _fontUpdateEvent.emit(target)
                            }
                        }
                    }
                } else {
                    FontTarget.entries.forEach { target ->
                        fontCache.remove(FontCacheKey(target, FontMode.FROM_FILE))
                        _fontUpdateEvent.emit(target)
                    }
                }
            }
        }
    }

    private suspend fun reloadRemoteFontToCache(target: FontTarget) {
        val typeface = fileStore.buildTypeface(target.targetFileName)
        fontCache[FontCacheKey(target, FontMode.FROM_FILE)] = typeface
        _fontUpdateEvent.emit(target)
    }

    private fun getBaseFont(
        target: FontTarget,
        mode: FontMode
    ): Typeface {
        val key = FontCacheKey(target, mode)

        return fontCache.getOrPut(key) {
            try {
                when (mode) {
                    FontMode.DEFAULT -> Typeface.DEFAULT_BOLD
                    FontMode.MI_SANS -> Typeface.Builder(vfDefaultPath).build()
                    FontMode.MI_SANS_CONDENSED -> Typeface.Builder(context.assets, Constants.ASSETS_VF_MI_SANS_CONDENSED).build()
                    FontMode.SF_PRO -> Typeface.Builder(context.assets, Constants.ASSETS_VF_SF_PRO).build()
                    FontMode.FROM_FILE -> Typeface.DEFAULT_BOLD
                }
            } catch (_: Exception) {
                Typeface.DEFAULT_BOLD
            }
        }
    }

    fun getNativeTypeface(
        target: FontTarget,
        mode: FontMode = FontMode.DEFAULT,
    ): Typeface {
        return getBaseFont(target, mode)
    }

    private suspend fun writeAndApplyFontBytes(
        bytes: ByteArray,
        target: FontTarget,
        displayFileName: String,
        originalPath: String
    ): Boolean {
        val success = fileStore.writeBytes(target.targetFileName, bytes)
        if (!success) return false

        prefRepo.update(target.displayNameSpKey, displayFileName)
        prefRepo.update(target.originalPathSpKey, originalPath)

        reloadRemoteFontToCache(target)
        return true
    }

    suspend fun importFontFromUri(uri: Uri, target: FontTarget): Boolean = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        try {
            val displayName = getFileNameFromUri(context, uri)

            val validExtensions = listOf(".ttf", ".otf", ".ttc")
            if (validExtensions.none { displayName.endsWith(it, ignoreCase = true) }) {
                MLog.e(TAG) { "Illegal file type $displayName" }
                return@withContext false
            }

            tempFile = File(context.cacheDir, "temp_import_${System.currentTimeMillis()}.ttf")

            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return@withContext false

            val bytes = tempFile.readBytes()

            return@withContext writeAndApplyFontBytes(bytes, target, displayName, displayName)
        } catch (e: Exception) {
            MLog.e(TAG, e) { "Error importing font from URI" }
            return@withContext false
        } finally {
            tempFile?.delete()
        }
    }


    suspend fun applyFontFromPath(path: String, target: FontTarget): Boolean = withContext(Dispatchers.IO) {
        try {
            if (path == vfDefaultPath) {
                resetToDefault(target)
                return@withContext true
            }

            val file = File(path)
            val bytes: ByteArray

            if (file.canRead()) {
                bytes = file.readBytes()
            } else {
                val relayTempFile = File(context.cacheDir, "root_relay_temp.ttf")

                val result = SystemCommander.execAsync(
                    "cp -f '$path' '${relayTempFile.absolutePath}' && chmod 666 '${relayTempFile.absolutePath}'",
                    useRoot = true,
                    silent = true
                )

                if (!result.isSuccess || !relayTempFile.canRead()) {
                    MLog.e(TAG) { "Root copy also failed for path: $path" }
                    MLog.e(TAG) { result.err }
                    return@withContext false
                }

                bytes = relayTempFile.readBytes()
                relayTempFile.delete()
            }

            val displayFileName = path.substringAfterLast("/")

            return@withContext writeAndApplyFontBytes(bytes, target, displayFileName, path)

        } catch (e: Exception) {
            MLog.e(TAG, e) { "Error applying font from path: $path" }
            return@withContext false
        }
    }

    suspend fun resetToDefault(target: FontTarget) = withContext(Dispatchers.IO) {
        prefRepo.update(target.displayNameSpKey, Constants.VARIABLE_FONT_DEFAULT_PATH)
        prefRepo.update(target.originalPathSpKey, Constants.VARIABLE_FONT_DEFAULT_PATH)
        fontCache.keys.removeIf { it.target == target }
        _fontUpdateEvent.emit(target)
        fileStore.delete(target.targetFileName)
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                } catch (t: Throwable) {
                    MLog.e(t)
                }
            }
        }
        if (result == null) {
            result = uri.path?.let { File(it).name }
        }
        return result ?: "UNKNOWN"
    }
}