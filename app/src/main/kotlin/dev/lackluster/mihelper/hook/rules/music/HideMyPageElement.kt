package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideMyPageElement : YukiBaseHooker() {
    override fun onHook() {
        "com.tencent.qqmusiclite.fragment.my.MyViewModel".toClassOrNull()?.apply {
            hasEnable(Pref.Key.Music.MY_HIDE_BANNER) {
                resolve().firstMethodOrNull {
                    name = "getMyBannerCard"
                }?.hook {
                    intercept()
                }
            }
            hasEnable(Pref.Key.Music.MY_HIDE_REC_PLAYLIST) {
                resolve().firstMethodOrNull {
                    name = "requestRecommendSongs"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}