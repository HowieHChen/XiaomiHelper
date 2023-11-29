package dev.lackluster.mihelper.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.hook.apps.Browser
import dev.lackluster.mihelper.hook.apps.Casting
import dev.lackluster.mihelper.hook.apps.MiSettings
import dev.lackluster.mihelper.hook.apps.MiShare
import dev.lackluster.mihelper.hook.apps.MiSmartHub
import dev.lackluster.mihelper.hook.apps.MiuiHome
import dev.lackluster.mihelper.hook.apps.PersonalAssist
import dev.lackluster.mihelper.hook.apps.PowerKeeper
import dev.lackluster.mihelper.hook.apps.SecurityCenter
import dev.lackluster.mihelper.hook.apps.Settings
import dev.lackluster.mihelper.hook.apps.Taplus
import dev.lackluster.mihelper.utils.Prefs

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
            // loadSystem(Android)
            loadApp(Scope.BROWSER, Browser)
            loadApp(Scope.CASTING, Casting)
            loadApp(Scope.MI_SETTINGS, MiSettings)
            loadApp(Scope.MI_SMART_HUB, MiSmartHub)
            loadApp(Scope.MISHARE, MiShare)
            loadApp(Scope.MIUI_HOME, MiuiHome)
            loadApp(Scope.PERSONAL_ASSIST, PersonalAssist)
            loadApp(Scope.POWER_KEEPER, PowerKeeper)
            loadApp(Scope.SECURITY_CENTER, SecurityCenter)
            loadApp(Scope.SETTINGS, Settings)
            loadApp(Scope.TAPLUS, Taplus)
        }
    }
}