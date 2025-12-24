package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideFavNum : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Music.HIDE_FAV_NUM) {
            // 首页推荐
            "com.tencent.qqmusiclite.model.shelfcard.Card".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "getSongFavNum"
                }?.hook {
                    intercept()
                }
            }
            // 二级页
            "com.tencent.qqmusiclite.ui.SongItemNewKt".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "needShowFavNum"
                }?.hook {
                    replaceToFalse()
                }
            }
            // 播放页
            "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
                val viewModel = resolve().firstFieldOrNull {
                    name = "viewModel"
                }
                val getViewLifecycleOwner = resolve().firstMethodOrNull {
                    name = "getViewLifecycleOwner"
                    superclass()
                }
                val clzPlayerSongViewModel = "com.tencent.qqmusiclite.activity.player.song.PlayerSongViewModel".toClassOrNull()
                val getFavorNumLiveData = clzPlayerSongViewModel?.resolve()?.firstMethodOrNull {
                    name = "getFavorNumLiveData"
                }
                val getFavorNumCacheLiveData = clzPlayerSongViewModel?.resolve()?.firstMethodOrNull {
                    name = "getFavorNumCacheLiveData"
                }
                val removeObservers = "androidx.lifecycle.MutableLiveData".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "removeObservers"
                    superclass()
                }
                resolve().firstMethodOrNull {
                    name = "viewModelDataSet"
                }?.hook {
                    after {
                        val viewLifecycleOwner = getViewLifecycleOwner?.copy()?.of(this.instance)?.invoke() ?: return@after
                        val vm = viewModel?.copy()?.of(this.instance)?.get() ?: return@after
                        val favorNumLiveData = getFavorNumLiveData?.copy()?.of(vm)?.invoke() ?: return@after
                        removeObservers?.copy()?.of(favorNumLiveData)?.invoke(viewLifecycleOwner)
                        val favorNumCacheLiveData = getFavorNumCacheLiveData?.copy()?.of(vm)?.invoke() ?: return@after
                        removeObservers?.copy()?.of(favorNumCacheLiveData)?.invoke(viewLifecycleOwner)
                    }
                }
            }
            // 歌词页
            "com.tencent.qqmusiclite.activity.player.lyric.PlayerLyricFragment".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "observeFavNumLiveData"
                }?.hook {
                    intercept()
                }
                resolve().firstMethodOrNull {
                    name = "removeObserveFavNumLiveData"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}