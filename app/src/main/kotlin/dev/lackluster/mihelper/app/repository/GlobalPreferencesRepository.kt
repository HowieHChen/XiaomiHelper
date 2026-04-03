package dev.lackluster.mihelper.app.repository

import android.content.Context
import android.net.Uri
import dev.lackluster.hyperx.core.SafeSP
import dev.lackluster.hyperx.ui.layout.HyperXLayoutConfig
import dev.lackluster.mihelper.app.state.AppEnvState
import dev.lackluster.mihelper.app.utils.getPref
import dev.lackluster.mihelper.app.utils.putPref
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.utils.factory.getSP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import androidx.core.content.edit
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GlobalPreferencesRepository(
    private val context: Context
) {
    private val _uiConfigFlow = MutableStateFlow(HyperXLayoutConfig())
    val uiConfigFlow = _uiConfigFlow.asStateFlow()

    private val _envStateFlow = MutableStateFlow(AppEnvState())
    val envStateFlow = _envStateFlow.asStateFlow()

    private val _globalReloadEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val globalReloadEvent = _globalReloadEvent.asSharedFlow()

    private val _preferenceUpdates = MutableSharedFlow<PreferenceKey<*>>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val preferenceUpdates = _preferenceUpdates.asSharedFlow()

    init {
        initAndCheck()
    }

    private fun initAndCheck() {
        try {
            SafeSP.setSP(getSP(context))
            versionCompatible()
            _envStateFlow.value = AppEnvState(
                isModuleActivated = true,
                isModuleEnabled = getPref(Preferences.App.MODULE_ENABLED),
                isRootIgnored = getPref(Preferences.App.SKIP_ROOT_CHECK),
                isRootGranted = true// SystemCommander.hasRootPrivilege
            )

            _uiConfigFlow.value = HyperXLayoutConfig(
                isSplitScreenEnabled = getPref(Preferences.App.ENABLE_SPLIT_SCREEN),
                isBlurEnabled = getPref(Preferences.App.HAZE_BLUR),
                lightBlurAlpha = getPref(Preferences.App.HAZE_LIGHT_BLUR_ALPHA),
                darkBlurAlpha = getPref(Preferences.App.HAZE_DARK_BLUR_ALPHA)
            )

        } catch (_: SecurityException) {
            _envStateFlow.value = AppEnvState(
                isModuleActivated = false,
                isModuleEnabled = Preferences.App.MODULE_ENABLED.default,
                isRootIgnored = Preferences.App.SKIP_ROOT_CHECK.default,
                isRootGranted = false
            )

            _uiConfigFlow.value = HyperXLayoutConfig(
                isBlurEnabled = Preferences.App.HAZE_BLUR.default,
                lightBlurAlpha = Preferences.App.HAZE_LIGHT_BLUR_ALPHA.default,
                darkBlurAlpha = Preferences.App.HAZE_DARK_BLUR_ALPHA.default,
                isSplitScreenEnabled = Preferences.App.ENABLE_SPLIT_SCREEN.default
            )
        }
    }

    private fun versionCompatible() {

    }

    suspend fun checkEnvironment() = withContext(Dispatchers.Default) {
        SystemCommander.requireRootAccess()
        val hasRoot = SystemCommander.hasRootPrivilege
        val isXposedActive = true // checkModuleActive()

        _envStateFlow.update {
            it.copy(
                isRootGranted = hasRoot,
                isModuleActivated = isXposedActive,
                isModuleEnabled = getPref(Preferences.App.MODULE_ENABLED),
                isRootIgnored = getPref(Preferences.App.SKIP_ROOT_CHECK)
            )
        }
    }

    fun <T: Any> get(key: PreferenceKey<T>): T {
        return getPref(key)
    }

    fun <T: Any> update(key: PreferenceKey<T>, value: T) {
        // 1. 统一持久化写入 SP (完全不用写一堆 if)
        putPref(key, value)

        _preferenceUpdates.tryEmit(key)

        // 2. 💥 精准的局部状态更新 (利用 when 优雅分发)
        when (key) {
            // UI 相关状态更新
            Preferences.App.ENABLE_SPLIT_SCREEN -> _uiConfigFlow.update { it.copy(isSplitScreenEnabled = value as Boolean) }
            Preferences.App.HAZE_BLUR -> _uiConfigFlow.update { it.copy(isBlurEnabled = value as Boolean) }
            Preferences.App.HAZE_LIGHT_BLUR_ALPHA -> _uiConfigFlow.update { it.copy(lightBlurAlpha = value as Float) }
            Preferences.App.HAZE_DARK_BLUR_ALPHA -> _uiConfigFlow.update { it.copy(darkBlurAlpha = value as Float) }

            // 环境偏好状态更新
            Preferences.App.MODULE_ENABLED -> _envStateFlow.update { it.copy(isModuleEnabled = value as Boolean) }
            Preferences.App.SKIP_ROOT_CHECK -> _envStateFlow.update { it.copy(isRootIgnored = value as Boolean) }

            // 如果是几百个不需要全局 State 监听的 Hook 规则 Key，这里根本不用写！
            // 因为第 1 步已经存入 SP 了，Hook 端可以直接读到。
        }
    }

    suspend fun exportBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sp = SafeSP.getSP() ?: throw IllegalStateException("SharedPreferences not initialized")
            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: throw IOException("Can't open output stream")

            val jsonObject = JSONObject()
            for ((key, value) in sp.all) {
                when (value) {
                    is Int -> jsonObject.put(key, "#i#$value")
                    is Float -> jsonObject.put(key, "#f#$value")
                    is String -> jsonObject.put(key, value)
                    is Boolean -> jsonObject.put(key, value)
                    is Long -> jsonObject.put(key, value)
                    is Set<*> -> jsonObject.put(key, value.joinToString(",", "[", "]"))
                    else -> jsonObject.put(key, value)
                }
            }

            outputStream.bufferedWriter().use { it.write(jsonObject.toString()) }
        }
    }

    suspend fun importBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sp = SafeSP.getSP() ?: throw IllegalStateException("SharedPreferences not initialized")
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Can't open input stream")

            val allText = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(allText)

            if (!checkBackupFileValid(jsonObject)) {
                throw IllegalArgumentException("Unsupported backup version or invalid file format")
            }

            sp.edit(commit = true) {
                for (key in jsonObject.keys()) {
                    when (val value = jsonObject.get(key)) {
                        is Boolean -> putBoolean(key, value)
                        is Int -> putInt(key, value)
                        is Float -> putFloat(key, value)
                        is String -> {
                            if (value.startsWith("[") && value.endsWith("]")) {
                                val stringList =
                                    value.removeSurrounding("[", "]").replace(" ", "").split(",")
                                val stringSet = HashSet(stringList)
                                putStringSet(key, stringSet)
                            } else if (value.startsWith("#i#")) {
                                putInt(key, value.removePrefix("#i#").toInt())
                            } else if (value.startsWith("#f#") || value.startsWith("#f")) {
                                // 兼容旧代码里可能少个 '#' 的情况
                                val floatStr = value.removePrefix("#f#").removePrefix("#f")
                                putFloat(key, floatStr.toFloat())
                            } else {
                                putString(key, value)
                            }
                        }
                    }
                }
            }
            initAndCheck()
            _globalReloadEvent.emit(Unit)
        }
    }

    suspend fun resetSettings(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sp = SafeSP.getSP() ?: throw IllegalStateException("SharedPreferences not initialized")
            val editor = sp.edit()
            editor.clear()
            if (!editor.commit()) {
                throw IOException("Failed to commit SharedPreferences reset")
            }
            initAndCheck()
            _globalReloadEvent.emit(Unit)
        }
    }

    private fun checkBackupFileValid(jsonObject: JSONObject): Boolean {
        val versionKey = Preferences.Module.SP_VERSION.name

        if (!jsonObject.has(versionKey)) {
            return true
        }

        return try {
            when (val value = jsonObject.get(versionKey)) {
                is Int -> true
                is String -> {
                    val version = value.removePrefix("#i#").toInt()
                    version <= Preferences.VERSION
                }
                else -> false
            }
        } catch (_: Exception) {
            false
        }
    }
}