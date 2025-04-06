package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object HideTopTab : YukiBaseHooker() {
    private const val TOP_TAB_HOME_ID = 1
    private const val TOP_TAB_KEGE_ID = 2
    private const val TOP_TAB_LONG_AUDIO_ID = 3
    private const val TOP_TAB_QUICK_PLAY_ID = 4
    private val hideKaraoke = Prefs.getBoolean(Pref.Key.Music.HIDE_KARAOKE, false)
    private val hideLongAudio = Prefs.getBoolean(Pref.Key.Music.HIDE_LONG_AUDIO, false)
    private val hideDiscover = Prefs.getBoolean(Pref.Key.Music.HIDE_DISCOVER, false)
    private val topTabClass by lazy {
        "com.tencent.qqmusiclite.data.dto.shelfcard2.TopTab".toClassOrNull()
    }

    override fun onHook() {
        if (hideKaraoke || hideLongAudio || hideDiscover) {
            val tabIdField = topTabClass?.field {
                name = "id"
                type = IntClass
            }
            "com.tencent.qqmusiclite.fragment.home.BaseHomeFragment".toClassOrNull()?.apply {
                if (hideLongAudio) {
                    method {
                        name = "setupViewPager"
                    }.hook {
                        before {
                            this.instance.current(true).field {
                                name = "mIsLongAudioEnable"
                            }.setFalse()
                        }
                    }
                }
                method {
                    name = "getTabs"
                }.hook {
                    after {
                        val list = (this.result as List<*>).toMutableList()
                        this.result = list.filter {
                            val id = tabIdField?.get(it)?.int() ?: return@filter true
                            when (id) {
                                TOP_TAB_HOME_ID -> true
                                TOP_TAB_KEGE_ID -> !hideKaraoke
                                TOP_TAB_LONG_AUDIO_ID -> !hideLongAudio
                                TOP_TAB_QUICK_PLAY_ID -> !hideDiscover
                                else -> true
                            }
                        }.toMutableList()
                    }
                }
            }
        }
    }
}