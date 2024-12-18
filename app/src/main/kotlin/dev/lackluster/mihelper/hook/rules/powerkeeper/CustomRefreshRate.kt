package dev.lackluster.mihelper.hook.rules.powerkeeper

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomRefreshRate : YukiBaseHooker() {
    private val parseCustomModeMethod by lazy {
        DexKit.findMethodsWithCache("custom_refresh_rate") {
            matcher {
                addUsingString("custom_mode_switch", StringMatchType.Equals)
                addUsingString("fucSwitch", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.PowerKeeper.UNLOCK_CUSTOM_REFRESH) {
            if (appClassLoader == null) return@hasEnable
            parseCustomModeMethod.map { it.getMethodInstance(appClassLoader!!) }.hookAll {
                before {
                    this.instance.current().field {
                        name = "mIsCustomFpsSwitch"
                    }.set(true.toString())
                }
            }
        }
    }
}