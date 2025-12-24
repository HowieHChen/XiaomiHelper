package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
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
    private val clzTopTab by lazy {
        "com.tencent.qqmusiclite.data.dto.shelfcard2.TopTab".toClassOrNull()
    }
    private val fldTabId by lazy {
        clzTopTab?.resolve()?.firstFieldOrNull {
            name = "id"
        }?.self?.apply {
            isAccessible = true
        }
    }

    override fun onHook() {
        if (hideKaraoke || hideLongAudio || hideDiscover) {
            "com.tencent.qqmusiclite.fragment.home.BaseHomeFragment".toClassOrNull()?.apply {
                if (hideLongAudio) {
                    val mIsLongAudioEnable = resolve().firstFieldOrNull {
                        name = "mIsLongAudioEnable"
                    }
                    resolve().firstMethodOrNull {
                        name = "setupViewPager"
                    }?.hook {
                        before {
                            mIsLongAudioEnable?.copy()?.of(this.instance)?.set(false)
                        }
                    }
                }
                resolve().firstMethodOrNull {
                    name = "getTabs"
                }?.hook {
                    after {
                        val list = (this.result as List<*>).toMutableList()
                        this.result = list.filter {
                            val id = (fldTabId?.get(it) as? Int) ?: return@filter true
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