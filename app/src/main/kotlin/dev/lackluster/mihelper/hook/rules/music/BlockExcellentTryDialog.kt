package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object BlockExcellentTryDialog : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Music.BLOCK_TRY_EXCELLENT) {
            "com.tencent.qqmusiclite.freemode.ExcellentTryStrategy".toClassOrNull()?.apply {
                method {
                    name = "tryShowExcellentGuideAlert"
                }.give()?.let {
                    XposedBridge.hookMethod(
                        it,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = false as Any?
                            }
                        }
                    )
                }
                method {
                    name = "checkNeedShowExcellentGuideAlert"
                }.give()?.let {
                    XposedBridge.hookMethod(
                        it,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                param?.result = false as Any?
                            }
                        }
                    )
                }
            }
        }
    }
}