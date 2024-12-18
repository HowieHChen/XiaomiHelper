package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object HideMyPageElement : YukiBaseHooker() {
    private val hideBanner = Prefs.getBoolean(Pref.Key.Music.MY_HIDE_BANNER, false)
    private val hideRecPlaylist = Prefs.getBoolean(Pref.Key.Music.MY_HIDE_REC_PLAYLIST, false)
    private val myViewModelClass by lazy {
        "com.tencent.qqmusiclite.fragment.my.MyViewModel".toClassOrNull()
    }

    override fun onHook() {
        if (hideBanner) {
            myViewModelClass?.apply {
                method {
                    method {
                        name = "getMyBannerCard"
                    }.hook {
                        intercept()
                    }
                }
            }
        }
        if (hideRecPlaylist) {
            myViewModelClass?.apply {
                method {
                    name = "requestRecommendSongs"
                }.hook {
                    intercept()
                }
            }
        }
    }
}