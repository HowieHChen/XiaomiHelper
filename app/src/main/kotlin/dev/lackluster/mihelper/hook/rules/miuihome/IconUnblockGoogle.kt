package dev.lackluster.mihelper.hook.rules.miuihome

import android.content.ComponentName
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object IconUnblockGoogle : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_ICON_UNBLOCK_GOOGLE) {
            "com.miui.home.launcher.AppFilter".toClass()
                .constructor()
                .hookAll {
                    after {
                        this.instance.current().field {
                            name = "mSkippedItems"
                            modifiers {
                                isStatic
                            }
                        }.cast<HashSet<ComponentName>>()!!.removeIf {
                            it.packageName in setOf(
                                "com.google.android.googlequicksearchbox",
                                "com.google.android.gms"
                            )
                        }
                    }
                }
        }
    }
}