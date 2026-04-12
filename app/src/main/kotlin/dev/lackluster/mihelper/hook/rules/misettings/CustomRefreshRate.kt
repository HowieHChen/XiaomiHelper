package dev.lackluster.mihelper.hook.rules.misettings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : StaticHooker() {
    private val initPreferenceMethod by lazy {
        DexKit.findMethodWithCache("custom_high_refresh_rate") {
            matcher {
                addUsingString("btn_preferce_category", StringMatchType.Equals)
                paramCount = 1
                paramTypes("boolean")
            }
        }
    }

    override fun onInit() {
        Preferences.PowerKeeper.UNLOCK_CUSTOM_REFRESH.get().also {
            updateSelfState(it)
        }.ifTrue {
            initPreferenceMethod
        }
    }

    override fun onHook() {
        initPreferenceMethod?.getMethodInstance(classLoader)?.hook {
            result(true)
        }
        "com.xiaomi.misettings.display.RefreshRate.RefreshRateActivity".toClassOrNull()?.apply {
            resolve().firstFieldOrNull {
                type("java.lang.Boolean")
                modifiers {
                    it.contains(Modifiers.STATIC) && it.contains(Modifiers.FINAL)
                }
            }?.set(true)
        }
    }
}