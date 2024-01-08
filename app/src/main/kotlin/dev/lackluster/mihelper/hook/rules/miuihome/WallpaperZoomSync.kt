package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object WallpaperZoomSync : YukiBaseHooker() {
    private var allowZoom = false
    override fun onHook() {
        hasEnable(PrefKey.HOME_WALLPAPER_ZOOM_SYNC) {
            val launcherClz = "com.miui.home.launcher.Launcher".toClass()
            val animateWallpaperZoomMethod =
                launcherClz.methods.first { it.name == "animateWallpaperZoom" }
            animateWallpaperZoomMethod.hook {
                before {
                    if (this.args(0).boolean()) {
                        if (allowZoom) {
                            allowZoom = false
                        }
                        else {
                            this.result = null
                        }
                    }
                }
            }
            "com.miui.home.recents.QuickstepAppTransitionManagerImpl".toClass()
                .method {
                    name = "startLauncherContentAnimator"
                }.hook {
                    before {
                        if (this.args(0).boolean()) {
                            allowZoom = true
                        }
                    }
                }
            "com.miui.home.recents.OverviewState".toClass()
                .method {
                    name = "onStateEnabled"
                }
                .hook {
                    before {
                        allowZoom = true
                    }
                }
            "com.miui.home.recents.NavStubView".toClass().apply {
                method {
                    name = "enterHomeHoldState"
                }.hook {
                    before {
                        allowZoom = true
                    }
                }
                method {
                    name = "exitRecentsHoldState"
                }.hook {
                    before {
                        allowZoom = true
                    }
                }
            }
            if (!Device.isPad) {
                "com.miui.home.recents.LauncherAppTransitionManagerImpl".toClass()
                    .method {
                        name = "composeRecentsLaunchAnimator"
                    }
                    .hook {
                        allowZoom = true
                    }
            }
        }
    }
}