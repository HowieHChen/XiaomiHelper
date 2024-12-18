package dev.lackluster.mihelper.hook.rules.guardprovider

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object BlockEnvCheck : YukiBaseHooker() {
    private val checkRootMethod by lazy {
        DexKit.findMethodWithCache("root_check") {
            matcher {
                returnType = "boolean"
                addUsingString("/system/bin/su", StringMatchType.Equals)
                addUsingString("/system/xbin/su", StringMatchType.Equals)
            }
        }
    }
    override fun onHook() {
        hasEnable(Pref.Key.GuardProvider.BLOCK_ENV_CHECK) {
            if (appClassLoader == null) return@hasEnable
            checkRootMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
        }
    }
}