package dev.lackluster.mihelper.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.apps.Android
import dev.lackluster.mihelper.hook.apps.Browser
import dev.lackluster.mihelper.hook.apps.Download
import dev.lackluster.mihelper.hook.apps.DownloadUI
import dev.lackluster.mihelper.hook.apps.GuardProvider
import dev.lackluster.mihelper.hook.apps.InCallUI
import dev.lackluster.mihelper.hook.apps.LBE
import dev.lackluster.mihelper.hook.apps.Market
import dev.lackluster.mihelper.hook.apps.MiLink
import dev.lackluster.mihelper.hook.apps.MiSettings
import dev.lackluster.mihelper.hook.apps.MiMirror
import dev.lackluster.mihelper.hook.apps.MiuiHome
import dev.lackluster.mihelper.hook.apps.Mms
import dev.lackluster.mihelper.hook.apps.Music
import dev.lackluster.mihelper.hook.apps.PackageInstaller
import dev.lackluster.mihelper.hook.apps.PersonalAssist
import dev.lackluster.mihelper.hook.apps.PowerKeeper
import dev.lackluster.mihelper.hook.apps.SecurityCenter
import dev.lackluster.mihelper.hook.apps.Settings
import dev.lackluster.mihelper.hook.apps.SystemUI
import dev.lackluster.mihelper.hook.apps.Taplus
import dev.lackluster.mihelper.hook.apps.Themes
import dev.lackluster.mihelper.hook.apps.Updater
import dev.lackluster.mihelper.hook.apps.MiAi
import dev.lackluster.mihelper.hook.apps.MiTrust
import dev.lackluster.mihelper.hook.apps.RemoteController
import dev.lackluster.mihelper.hook.apps.Search
import dev.lackluster.mihelper.hook.apps.SystemUIPlugin
import dev.lackluster.mihelper.hook.apps.Weather
import dev.lackluster.mihelper.utils.factory.hasEnable

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "MiHelper"
        }
        isDebug = false
        isEnableHookSharedPreferences = true
    }

    override fun onHook() = encase {
        hasEnable(Pref.Key.Module.ENABLED) {
            loadSystem(Android)
            loadApp(Scope.BROWSER, Browser)
            loadApp(Scope.DOWNLOAD, Download)
            loadApp(Scope.DOWNLOAD_UI, DownloadUI)
            loadApp(Scope.GUARD_PROVIDER, GuardProvider)
            loadApp(Scope.IN_CALL_UI, InCallUI)
            loadApp(Scope.LBE, LBE)
            loadApp(Scope.MARKET, Market)
            loadApp(Scope.MI_AI, MiAi)
            loadApp(Scope.MI_LINK, MiLink)
            loadApp(Scope.MI_SETTINGS, MiSettings)
            loadApp(Scope.MI_MIRROR, MiMirror)
            loadApp(Scope.MI_TRUST, MiTrust)
            loadApp(Scope.MIUI_HOME, MiuiHome)
            loadApp(Scope.MMS, Mms)
            loadApp(Scope.MUSIC, Music)
            loadApp(Scope.PACKAGE_INSTALLER, PackageInstaller)
            loadApp(Scope.PERSONAL_ASSIST, PersonalAssist)
            loadApp(Scope.POWER_KEEPER, PowerKeeper)
            loadApp(Scope.REMOTE_CONTROLLER, RemoteController)
            loadApp(Scope.SEARCH, Search)
            loadApp(Scope.SECURITY_CENTER, SecurityCenter)
            loadApp(Scope.SETTINGS, Settings)
            loadApp(Scope.SYSTEM_UI, SystemUI)
            loadApp(Scope.TAPLUS, Taplus)
            loadApp(Scope.THEMES, Themes)
            loadApp(Scope.UPDATER, Updater)
            loadApp(Scope.WEATHER, Weather)
            loadApp(Scope.SYSTEM_UI_PLUGIN, SystemUIPlugin)
        }
    }
}