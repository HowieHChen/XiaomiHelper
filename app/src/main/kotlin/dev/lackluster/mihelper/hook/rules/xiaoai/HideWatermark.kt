package dev.lackluster.mihelper.hook.rules.xiaoai

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object HideWatermark : YukiBaseHooker(){
    private val addWatermark by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("add watermark", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.XIAOAI_HIDE_WATERMARK) {
            addWatermark?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                intercept()
            }
        }
    }
}