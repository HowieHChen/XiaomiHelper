package dev.lackluster.mihelper.app.manager

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.state.AppEnvState
import dev.lackluster.mihelper.app.state.XposedState
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.MLog
import dev.lackluster.mihelper.utils.SystemProperties
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AppEnvironmentManager"

class AppEnvironmentManager(
    private val xposedManager: XposedServiceManager,
    private val prefRepo: GlobalPreferencesRepository
) {
    private val _envStateFlow = MutableStateFlow(
        AppEnvState(
            isModuleActivated = false,
            isModuleEnabled = Preferences.Module.MODULE_ENABLED.default,
            isRootGranted = false,
            isRootIgnored = Preferences.App.SKIP_ROOT_CHECK.default,
            isSystemVersionSupported = true,
            isSystemVersionWarningIgnored = prefRepo.get(Preferences.App.SKIP_SYSTEM_VERSION_WARNING),
        )
    )
    val envStateFlow = _envStateFlow.asStateFlow()
    
    private val _xposedStateFlow = MutableStateFlow(
        XposedState()
    )
    val xposedState = _xposedStateFlow.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            xposedManager.serviceFlow.collect { service ->
                if (service != null) {
                    val validVersion = service.apiVersion >= 101
                    val cap = service.frameworkProperties
                    val activated = validVersion && (cap.and(XposedService.PROP_CAP_SYSTEM) != 0L) && (cap.and(XposedService.PROP_CAP_REMOTE) != 0L)
                    MLog.i(TAG) {
                        "XposedService： " +
                                "apiVersion: ${service.apiVersion}, frameworkName: ${service.frameworkName}, " + 
                                "frameworkVersion: ${service.frameworkVersion}, frameworkVersionCode: ${service.frameworkVersionCode}" 
                    }
                    MLog.i(TAG) {
                        "XposedService： " +
                                "frameworkProperties=${cap}, " + 
                                "PROP_CAP_SYSTEM=${(cap.and(XposedService.PROP_CAP_SYSTEM) != 0L)}, " +
                                "PROP_CAP_REMOTE=${(cap.and(XposedService.PROP_CAP_REMOTE) != 0L)}"
                    }
                    MLog.i(TAG) {
                        "XposedService： " +
                                "scopes=[${service.scope.joinToString(", ")}]"
                    }
                    refreshState(activated)
                    _xposedStateFlow.update {
                        XposedState(
                            apiVersion = service.apiVersion,
                            frameworkName = service.frameworkName,
                            frameworkVersion = service.frameworkVersion,
                            frameworkVersionCode = service.frameworkVersionCode,
                            frameworkProperties = service.frameworkProperties
                        )
                    }
                } else {
                    refreshState(false)
                    _xposedStateFlow.update { XposedState() }
                }
            }
        }

        scope.launch {
            prefRepo.preferenceUpdates.collect { key ->
                if (key == Preferences.Module.MODULE_ENABLED ||
                    key == Preferences.App.SKIP_ROOT_CHECK ||
                    key == Preferences.App.SKIP_SYSTEM_VERSION_WARNING
                ) {
                    _envStateFlow.update {
                        it.copy(
                            isModuleEnabled = prefRepo.get(Preferences.Module.MODULE_ENABLED),
                            isRootIgnored = prefRepo.get(Preferences.App.SKIP_ROOT_CHECK),
                            isSystemVersionWarningIgnored = prefRepo.get(Preferences.App.SKIP_SYSTEM_VERSION_WARNING)
                        )
                    }
                }
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                MLog.d(TAG) { "App returned to foreground, environment refreshed." }
                checkRoot()
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)

        checkRoot()
        checkSystemVersion()
    }

    fun checkRoot() {
        CoroutineScope(Dispatchers.IO).launch {
            SystemCommander.requireRootAccess()
            val hasRoot = SystemCommander.hasRootPrivilege
            _envStateFlow.update { it.copy(isRootGranted = hasRoot) }
        }
    }

    fun ignoreSystemVersionWarning() {
        scope.launch {
            prefRepo.update(Preferences.App.SKIP_SYSTEM_VERSION_WARNING, true)
        }
    }

    private fun checkSystemVersion() {
        scope.launch {
            val fingerprint = SystemProperties.get("ro.build.fingerprint")
            _envStateFlow.update {
                it.copy(isSystemVersionSupported = isSystemVersionSupported(fingerprint))
            }
        }
    }

    private fun refreshState(isXposedActive: Boolean) {
        _envStateFlow.update {
            it.copy(
                isModuleActivated = isXposedActive,
                isModuleEnabled = prefRepo.get(Preferences.Module.MODULE_ENABLED),
                isRootGranted = SystemCommander.hasRootPrivilege,
                isRootIgnored = prefRepo.get(Preferences.App.SKIP_ROOT_CHECK),
                isSystemVersionWarningIgnored = prefRepo.get(Preferences.App.SKIP_SYSTEM_VERSION_WARNING)
            )
        }
    }

    private fun isSystemVersionSupported(fingerprint: String): Boolean {
        MLog.i(TAG) { "ro.build.fingerprint=$fingerprint" }
        val match = FINGERPRINT_VERSION_PATTERN.find(fingerprint) ?: return true
        val androidVersion = match.groupValues[1].toIntOrNull() ?: return true
        val hyperOsVersion = match.groupValues.drop(2).mapNotNull(String::toIntOrNull)
        if (hyperOsVersion.size != 3) return true
        val (major, minor, patch) = hyperOsVersion

        MLog.i(TAG) { "Android ${androidVersion}, HyperOS ${major}.${minor}.${patch}" }

        return androidVersion >= 16 && major >= 3 && minor >=0 && patch >= 300
    }

    private companion object {
        val FINGERPRINT_VERSION_PATTERN =
            Regex(""":(\d+)/[^/]+/OS(\d+)\.(\d+)\.(\d+)\.\d+\.[^/:]+:""")
    }
}