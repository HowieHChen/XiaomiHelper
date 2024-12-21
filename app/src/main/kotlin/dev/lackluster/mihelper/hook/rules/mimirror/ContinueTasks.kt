package dev.lackluster.mihelper.hook.rules.mimirror

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object ContinueTasks : YukiBaseHooker() {
    private val subScreen by lazy {
        DexKit.findMethodWithCache("pref_all_app_sub_screen") {
            matcher {
                addUsingString("support_all_app_sub_screen", StringMatchType.Equals)
                returnType = "boolean"
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiMirror.CONTINUE_ALL_TASKS) {
            if (appClassLoader == null) return@hasEnable
            subScreen?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToTrue()
            }
        }
    }
}