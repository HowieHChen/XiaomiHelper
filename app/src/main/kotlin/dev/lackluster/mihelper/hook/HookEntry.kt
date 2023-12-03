package dev.lackluster.mihelper.hook

import android.content.res.XResources
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.apps.Android
import dev.lackluster.mihelper.hook.apps.Browser
import dev.lackluster.mihelper.hook.apps.Casting
import dev.lackluster.mihelper.hook.apps.Download
import dev.lackluster.mihelper.hook.apps.Gallery
import dev.lackluster.mihelper.hook.apps.GuardProvider
import dev.lackluster.mihelper.hook.apps.InCallUI
import dev.lackluster.mihelper.hook.apps.Joyose
import dev.lackluster.mihelper.hook.apps.Market
import dev.lackluster.mihelper.hook.apps.MediaEditor
import dev.lackluster.mihelper.hook.apps.MiSettings
import dev.lackluster.mihelper.hook.apps.MiShare
import dev.lackluster.mihelper.hook.apps.MiSmartHub
import dev.lackluster.mihelper.hook.apps.MiuiHome
import dev.lackluster.mihelper.hook.apps.Mms
import dev.lackluster.mihelper.hook.apps.Music
import dev.lackluster.mihelper.hook.apps.PackageInstaller
import dev.lackluster.mihelper.hook.apps.PersonalAssist
import dev.lackluster.mihelper.hook.apps.PowerKeeper
import dev.lackluster.mihelper.hook.apps.ScreenRecorder
import dev.lackluster.mihelper.hook.apps.Screenshot
import dev.lackluster.mihelper.hook.apps.SecurityCenter
import dev.lackluster.mihelper.hook.apps.Settings
import dev.lackluster.mihelper.hook.apps.SystemUI
import dev.lackluster.mihelper.hook.apps.Taplus
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "MiHelper"
        }
        isEnableHookSharedPreferences = true
    }

    override fun onHook() = encase {
        if (Prefs.getBoolean(PrefKey.ENABLE_MODULE, true)) {
            loadSystem(Android)
            loadApp(Scope.BROWSER, Browser)
            loadApp(Scope.CASTING, Casting)
            loadApp(Scope.DOWNLOAD, Download)
            loadApp(Scope.GALLERY, Gallery)
            loadApp(Scope.GUARD_PROVIDER, GuardProvider)
            loadApp(Scope.IN_CALL_UI, InCallUI)
            loadApp(Scope.JOYOSE, Joyose)
            loadApp(Scope.MARKET, Market)
            loadApp(Scope.MEDIA_EDITOR , MediaEditor)
            loadApp(Scope.MI_SETTINGS, MiSettings)
            loadApp(Scope.MI_SMART_HUB, MiSmartHub)
            loadApp(Scope.MISHARE, MiShare)
            loadApp(Scope.MIUI_HOME, MiuiHome)
            loadApp(Scope.MMS, Mms)
            loadApp(Scope.MUSIC, Music)
            loadApp(Scope.PACKAGE_INSTALLER, PackageInstaller)
            loadApp(Scope.PERSONAL_ASSIST, PersonalAssist)
            loadApp(Scope.POWER_KEEPER, PowerKeeper)
            loadApp(Scope.SCREEN_RECORDER, ScreenRecorder)
            loadApp(Scope.SCREENSHOT, Screenshot)
            loadApp(Scope.SECURITY_CENTER, SecurityCenter)
            loadApp(Scope.SETTINGS, Settings)
            loadApp(Scope.SYSTEM_UI, SystemUI)
            loadApp(Scope.TAPLUS, Taplus)
        }
    }
}