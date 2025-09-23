package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideFavNum : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Music.HIDE_FAV_NUM) {
            // 首页推荐
            "com.tencent.qqmusiclite.model.shelfcard.Card".toClassOrNull()?.apply {
                method {
                    name = "getSongFavNum"
                }.hook {
                    intercept()
                }
            }
            // 二级页
            "com.tencent.qqmusiclite.ui.SongItemNewKt".toClassOrNull()?.apply {
                method {
                    name = "needShowFavNum"
                }.hook {
                    replaceToFalse()
                }
            }
            // 播放页
            "com.tencent.qqmusiclite.activity.player.song.PlayerSongFragment".toClassOrNull()?.apply {
                method {
                    name = "viewModelDataSet"
                }.hook {
                    after {
                        val viewLifecycleOwner = this.instance.current().method {
                            name = "getViewLifecycleOwner"
                            superClass()
                        }.call()
                        val viewModel = this.instance.current().field {
                            name = "viewModel"
                        }.any() ?: return@after
                        val favorNumLiveData = viewModel.current().method {
                            name = "getFavorNumLiveData"
                        }.call() ?: return@after
                        favorNumLiveData.current().method {
                            name = "removeObservers"
                            superClass()
                        }.call(viewLifecycleOwner)
                        val favorNumCacheLiveData = viewModel.current().method {
                            name = "getFavorNumCacheLiveData"
                        }.call() ?: return@after
                        favorNumCacheLiveData.current().method {
                            name = "removeObservers"
                            superClass()
                        }.call(viewLifecycleOwner)
                    }
                }
            }
            // 歌词页
            "com.tencent.qqmusiclite.activity.player.lyric.PlayerLyricFragment".toClassOrNull()?.apply {
                method {
                    name = "observeFavNumLiveData"
                }.hook {
                    intercept()
                }
                method {
                    name = "removeObserveFavNumLiveData"
                }.hook {
                    intercept()
                }
            }
        }
    }
}