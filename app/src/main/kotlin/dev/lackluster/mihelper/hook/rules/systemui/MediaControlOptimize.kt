package dev.lackluster.mihelper.hook.rules.systemui

import android.graphics.drawable.Icon
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object MediaControlOptimize : YukiBaseHooker() {
    private var lastArtwork: Icon? = null
    override fun onHook() {
        hasEnable(PrefKey.SYSTEMUI_NOTIF_MC_OPTIMIZE) {
            "com.android.systemui.media.MediaControlPanel".toClassOrNull()
                ?.method {
                    name = "bindArtworkAndColors"
                }
                ?.hook {
                    before {
                        val icon = this.args(0).any()?.current()?.method {
                            name = "getArtwork"
                        }?.call() as? Icon
                        if (icon != null && icon == lastArtwork) {
                            return@before
                        }
                        lastArtwork = icon
                        this.args(2).setTrue()
                    }
                }
            "com.android.systemui.statusbar.notification.mediacontrol.MiuiMediaControlPanel".toClassOrNull()
                ?.method {
                    name = "setInfoText"
                }?.hook {
                    before {
                        val mediaViewHolderAppName = this.args(1).any()?.current()?.method {
                            name = "getAppName"
                        }?.call() as? TextView
                        val mediaDataApp = this.args(0).any()?.current()?.method {
                            name = "getApp"
                        }?.call() as? String
                        mediaViewHolderAppName?.text = mediaDataApp
                        this.result = null
                    }
                }
        }
    }
}