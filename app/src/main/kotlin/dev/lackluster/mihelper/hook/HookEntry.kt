package dev.lackluster.mihelper.hook

import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.scopes.SystemUI
import dev.lackluster.mihelper.hook.scopes.SecurityCenter
import dev.lackluster.mihelper.hook.scopes.PackageInstaller
import dev.lackluster.mihelper.hook.scopes.Browser
import dev.lackluster.mihelper.hook.scopes.Search
import dev.lackluster.mihelper.hook.scopes.PersonalAssist
import dev.lackluster.mihelper.hook.scopes.PowerKeeper
import dev.lackluster.mihelper.hook.scopes.Taplus
import dev.lackluster.mihelper.hook.scopes.Themes
import dev.lackluster.mihelper.hook.scopes.Updater
import dev.lackluster.mihelper.hook.scopes.InCallUI
import dev.lackluster.mihelper.hook.scopes.LBE
import dev.lackluster.mihelper.hook.scopes.Mms
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.base.HookParam
import dev.lackluster.mihelper.hook.scopes.AIEngine
import dev.lackluster.mihelper.hook.scopes.Android
import dev.lackluster.mihelper.hook.scopes.Download
import dev.lackluster.mihelper.hook.scopes.DownloadUI
import dev.lackluster.mihelper.hook.scopes.GuardProvider
import dev.lackluster.mihelper.hook.scopes.Market
import dev.lackluster.mihelper.hook.scopes.MiAi
import dev.lackluster.mihelper.hook.scopes.MiLink
import dev.lackluster.mihelper.hook.scopes.MiMirror
import dev.lackluster.mihelper.hook.scopes.MiSettings
import dev.lackluster.mihelper.hook.scopes.MiTrust
import dev.lackluster.mihelper.hook.scopes.MiuiHome
import dev.lackluster.mihelper.hook.scopes.Music
import dev.lackluster.mihelper.hook.scopes.RemoteController
import dev.lackluster.mihelper.hook.scopes.Settings
import dev.lackluster.mihelper.hook.scopes.SystemUIPlugin
import dev.lackluster.mihelper.hook.utils.RemotePreferences
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.utils.MLog
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.util.concurrent.ConcurrentHashMap

class HookEntry : XposedModule() {
    private var isModuleEnabled: Boolean = true
    private val injectedPackages = ConcurrentHashMap.newKeySet<String>()
    private lateinit var processName: String
    private var isSystemServer: Boolean = false

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        processName = param.processName
        isSystemServer = param.isSystemServer
        MLog.init(this)
        RemotePreferences.init(this)
        Preferences.Module.DEBUG.observe {
            MLog.isDebugEnabled = it
        }
        isModuleEnabled = Preferences.Module.MODULE_ENABLED.get()
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        if (!isModuleEnabled) return
        val hookParam = HookParam(
            processName = processName,
            packageName = Scope.SYSTEM,
            isSystemServer = isSystemServer
        )
        attachHooker(Android, param.classLoader, hookParam)
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (!isModuleEnabled) return
        if (!injectedPackages.add(param.packageName)) return

        val appHooker = getHookerByPackageName(param.packageName) ?: return

        val hookParam = HookParam(
            processName = processName,
            packageName = param.packageName,
            isSystemServer = isSystemServer,
            isFirstPackage = param.isFirstPackage,
            isPackageReady = false,
            appInfo = param.applicationInfo
        )
        attachHooker(appHooker, param.defaultClassLoader, hookParam)
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        if (!isModuleEnabled) return
    }

    private fun getHookerByPackageName(pkg: String): BaseHooker? = when(pkg) {
        Scope.AI_ENGINE -> AIEngine
        Scope.BROWSER -> Browser
        Scope.DOWNLOAD -> Download
        Scope.DOWNLOAD_UI -> DownloadUI
        Scope.GUARD_PROVIDER -> GuardProvider
        Scope.IN_CALL_UI -> InCallUI
        Scope.LBE -> LBE
        Scope.MARKET -> Market
        Scope.MI_AI -> MiAi
        Scope.MI_LINK -> MiLink
        Scope.MI_MIRROR -> MiMirror
        Scope.MI_TRUST -> MiTrust
        Scope.MIUI_HOME -> MiuiHome
        Scope.MI_SETTINGS -> MiSettings
        Scope.MMS -> Mms
        Scope.MUSIC -> Music
        Scope.PACKAGE_INSTALLER -> PackageInstaller
        Scope.PERSONAL_ASSIST -> PersonalAssist
        Scope.POWER_KEEPER -> PowerKeeper
        Scope.REMOTE_CONTROLLER -> RemoteController
        Scope.SEARCH -> Search
        Scope.SECURITY_CENTER -> SecurityCenter
        Scope.SETTINGS -> Settings
        Scope.SYSTEM_UI -> SystemUI
        Scope.SYSTEM_UI_PLUGIN -> SystemUIPlugin
        Scope.TAPLUS -> Taplus
        Scope.THEMES -> Themes
        Scope.UPDATER -> Updater
        else -> null
    }

    private fun attachHooker(
        hooker: BaseHooker,
        targetClassLoader: ClassLoader,
        param: HookParam,
    ) {
        hooker.module = this
        hooker.classLoader = targetClassLoader
        hooker.hookParam = param

        hooker.performInit()
        hooker.updateParentState(true)
    }

}