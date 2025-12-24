package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideListenCount : YukiBaseHooker() {
    private val cachedResult by lazy {
        "kotlin.Pair".toClassOrNull()?.resolve()?.firstConstructor {
            parameterCount = 2
        }?.create(false, "")
    }

    override fun onHook() {
        hasEnable(Pref.Key.Music.HIDE_LISTEN_COUNT) {
            "com.tencent.qqmusiclite.data.repo.playhistory.PlayHistoryRepo".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "shouldShowPlayCountAndGetPlayCount"
                }?.hook {
                    replaceTo(cachedResult)
                }
            }
        }
    }
}