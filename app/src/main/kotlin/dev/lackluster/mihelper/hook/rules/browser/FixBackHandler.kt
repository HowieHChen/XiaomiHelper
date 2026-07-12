package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.toTyped

object FixBackHandler : StaticHooker() {
    override fun onInit() {
        updateSelfState(true)
    }

    override fun onHook() {
        "com.android.browser.menu.PortraitMenuDialog".toClassOrNull()?.apply {
            val metOnHiddenChanged = resolve().firstMethodOrNull {
                name = "onHiddenChanged"
                parameters(Boolean::class)
                superclass()
            }?.toTyped<Unit>()
            val metIsHidden = resolve().firstMethodOrNull {
                name = "isHidden"
                parameterCount = 0
                returnType(Boolean::class)
                superclass()
            }?.toTyped<Boolean>()
            resolve().firstMethodOrNull {
                name = "onViewCreated"
            }?.hook {
                val ori = proceed()
                metOnHiddenChanged?.invoke(thisObject, metIsHidden?.invoke(thisObject) == true)
                result(ori)
            }
        }
    }
}