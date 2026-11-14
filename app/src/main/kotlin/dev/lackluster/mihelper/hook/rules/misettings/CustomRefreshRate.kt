package dev.lackluster.mihelper.hook.rules.misettings

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private var oriSupportSmartFps: Boolean? = null

    private val initPreferenceMethod by lazy {
        DexKit.findMethodWithCache("custom_high_refresh_rate") {
            matcher {
                addUsingString("btn_preferce_category", StringMatchType.Equals)
                paramCount = 1
                paramTypes("boolean")
            }
        }
    }

    private val fldSupportSmartFps by lazy {
        "com.xiaomi.misettings.display.RefreshRate.RefreshRateActivity".toClassOrNull()?.resolve()?.firstFieldOrNull {
            type("java.lang.Boolean")
            modifiers {
                it.contains(Modifiers.STATIC) && it.contains(Modifiers.FINAL)
            }
        }?.toTyped<Boolean>()
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.PowerKeeper.UNLOCK_CUSTOM_REFRESH.observe {
                updateSelfState(it)
            }
        )
        initPreferenceMethod
    }

    override fun onStart() {
        initPreferenceMethod?.getMethodInstance(classLoader)?.hook {
            val newArgs = args.toTypedArray()
            newArgs[0] = true
            result(proceed(newArgs))
        }
        oriSupportSmartFps = fldSupportSmartFps?.get(null)
        fldSupportSmartFps?.set(null, true)
    }

    override fun onStop() {
        oriSupportSmartFps?.let {
            fldSupportSmartFps?.set(null, it)
        }
    }
}