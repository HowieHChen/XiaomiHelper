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
            isRootIgnored = Preferences.App.SKIP_ROOT_CHECK.default
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
                    MLog.v(TAG) {
                        "XposedService： " +
                                "apiVersion: ${service.apiVersion}, frameworkName: ${service.frameworkName}, " + 
                                "frameworkVersion: ${service.frameworkVersion}, frameworkVersionCode: ${service.frameworkVersionCode}" 
                    }
                    MLog.v(TAG) {
                        "XposedService： " +
                                "frameworkProperties=${cap}, " + 
                                "PROP_CAP_SYSTEM=${(cap.and(XposedService.PROP_CAP_SYSTEM) != 0L)}, " +
                                "PROP_CAP_REMOTE=${(cap.and(XposedService.PROP_CAP_REMOTE) != 0L)}"
                    }
                    MLog.v(TAG) {
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
                if (key == Preferences.Module.MODULE_ENABLED || key == Preferences.App.SKIP_ROOT_CHECK) {
                    _envStateFlow.update {
                        it.copy(
                            isModuleEnabled = prefRepo.get(Preferences.Module.MODULE_ENABLED),
                            isRootIgnored = prefRepo.get(Preferences.App.SKIP_ROOT_CHECK)
                        )
                    }
                }
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                MLog.d { "App returned to foreground, environment refreshed." }
                checkRoot()
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)

        checkRoot()
    }

    fun checkRoot() {
        CoroutineScope(Dispatchers.IO).launch {
            SystemCommander.requireRootAccess()
            val hasRoot = SystemCommander.hasRootPrivilege
            _envStateFlow.update { it.copy(isRootGranted = hasRoot) }
        }
    }

    private fun refreshState(isXposedActive: Boolean) {
        _envStateFlow.update {
            it.copy(
                isModuleActivated = isXposedActive,
                isModuleEnabled = prefRepo.get(Preferences.Module.MODULE_ENABLED),
                isRootGranted = SystemCommander.hasRootPrivilege,
                isRootIgnored = prefRepo.get(Preferences.App.SKIP_ROOT_CHECK)
            )
        }
    }
}