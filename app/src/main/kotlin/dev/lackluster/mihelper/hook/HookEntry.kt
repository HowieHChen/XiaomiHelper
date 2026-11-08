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
import dev.lackluster.mihelper.hook.base.HookSession
import dev.lackluster.mihelper.hook.base.CleanupFailures
import dev.lackluster.mihelper.hook.scopes.AIEngine
import dev.lackluster.mihelper.hook.scopes.Android
import dev.lackluster.mihelper.hook.scopes.Download
import dev.lackluster.mihelper.hook.scopes.DownloadUI
import dev.lackluster.mihelper.hook.scopes.FindDevice
import dev.lackluster.mihelper.hook.scopes.GuardProvider
import dev.lackluster.mihelper.hook.scopes.Market
import dev.lackluster.mihelper.hook.scopes.MiAi
import dev.lackluster.mihelper.hook.scopes.MiIntentResolver
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
import dev.lackluster.mihelper.hook.utils.HostExecutor
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.utils.MLog
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class HookEntry : XposedModule() {
    private var isModuleEnabled: Boolean = true
    private lateinit var session: HookSession
    private lateinit var processName: String
    private var isSystemServer: Boolean = false

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        initializeGeneration(param)
    }

    private fun initializeGeneration(param: XposedModuleInterface.ModuleLoadedParam) {
        // 冷启动与新代共用初始化；热重载不会自动重放 onModuleLoaded，不能只在那个回调里准备服务。
        processName = param.processName
        isSystemServer = param.isSystemServer
        MLog.init(this)
        session = HookSession(this)
        RemotePreferences.init(this)
        Preferences.Module.DEBUG.observe {
            MLog.isDebugEnabled = it
        }
        isModuleEnabled = Preferences.Module.MODULE_ENABLED.get()
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        val hookParam = HookParam(
            processName = processName,
            packageName = Scope.SYSTEM,
            isSystemServer = isSystemServer
        )
        val host = HookSession.HostRecord(Scope.SYSTEM, param.classLoader, hookParam)
        // 总开关只控制业务安装，不阻止记录宿主；旧代零业务 Hook 时，新代也需要这些信息才能启用。
        session.recordHost(host)
        if (isModuleEnabled) session.attachRoot(Android, host)
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        val hookParam = HookParam(
            processName = processName,
            packageName = param.packageName,
            isSystemServer = isSystemServer,
            isFirstPackage = param.isFirstPackage,
            isPackageReady = false,
            appInfo = param.applicationInfo
        )
        val host = HookSession.HostRecord(param.packageName, param.defaultClassLoader, hookParam)
        // 与 system_server 一样，无论业务是否启用都保留加载信息。
        session.recordHost(host)
        if (isModuleEnabled) getHookerByPackageName(param.packageName)?.let { session.attachRoot(it, host) }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        // Ready 提供经过 AppComponentFactory 处理的加载器；只更新记录，不重复安装 Loaded 阶段的规则。
        val loaded = session.recordedHosts().firstOrNull { it.key == param.packageName }
        session.recordHost(HookSession.HostRecord(
            key = param.packageName,
            classLoader = loaded?.classLoader ?: param.defaultClassLoader,
            param = loaded?.param?.copy(isPackageReady = true) ?: HookParam(
                processName, param.packageName, isSystemServer, param.isFirstPackage, true, param.applicationInfo
            ),
            readyClassLoader = param.classLoader,
        ))
    }

    override fun onHotReloading(param: XposedModuleInterface.HotReloadingParam): Boolean {
        // 在任何销毁动作之前拒绝未适配的节点，避免检查到一半时已有功能被停止。
        val blockers = session.reloadBlockers()
        if (blockers.isNotEmpty()) {
            MLog.w { "Hot reload requires hooker migration: ${blockers.joinToString()}" }
            return false
        }
        val failures = CleanupFailures()
        var accepted = false
        failures.attempt { accepted = session.retireForReload { param.setSavedInstanceState(it) } }
        if (!session.isActive) {
            // 一旦进入退役，即使节点清理失败也继续关闭公共服务；等待线程退出放在 Session 锁之外。
            failures.attempt { RemotePreferences.close() }
            failures.attempt { check(HostExecutor.shutdown()) { "Module worker threads did not terminate; restart target" } }
        }
        failures.throwIfAny()
        return accepted
    }

    override fun onHotReloaded(param: XposedModuleInterface.HotReloadedParam) {
        initializeGeneration(param)
        try {
            session.beginReload(param.savedInstanceState, param.oldHookHandles)
            // 包加载回调不会重放：使用保存的宿主记录和当前配置，主动重建本代的规则树。
            if (isModuleEnabled) session.recordedHosts().forEach { host ->
                val hooker = if (host.key == Scope.SYSTEM) Android else getHookerByPackageName(host.param.packageName)
                hooker?.let { session.attachRoot(it, host) }
            }
            session.finishReload()
        } catch (failure: Throwable) {
            // 不承诺业务事务回滚；重建失败时尽力清理新资源和旧残留，保留原始失败原因。
            val cleanup = CleanupFailures()
            cleanup.attempt { session.dispose() }
            cleanup.attempt { RemotePreferences.close() }
            cleanup.attempt { check(HostExecutor.shutdown()) { "Module workers did not terminate" } }
            try { cleanup.throwIfAny() } catch (t: Throwable) { failure.addSuppressed(t) }
            throw failure
        }
    }

    private fun getHookerByPackageName(pkg: String): BaseHooker? = when(pkg) {
        Scope.AI_ENGINE -> AIEngine
        Scope.BROWSER -> Browser
        Scope.DOWNLOAD -> Download
        Scope.DOWNLOAD_UI -> DownloadUI
        Scope.FIND_DEVICE -> FindDevice
        Scope.GUARD_PROVIDER -> GuardProvider
        Scope.IN_CALL_UI -> InCallUI
        Scope.LBE -> LBE
        Scope.MARKET -> Market
        Scope.MI_AI -> MiAi
        Scope.MI_INTENT_RESOLVER -> MiIntentResolver
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

}
