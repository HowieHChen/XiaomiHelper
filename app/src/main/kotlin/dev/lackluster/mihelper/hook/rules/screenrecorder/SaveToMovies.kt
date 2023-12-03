package dev.lackluster.mihelper.hook.rules.screenrecorder

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.hook.rules.miuihome.BlurEnableAll.toClass
import dev.lackluster.mihelper.hook.rules.screenshot.SaveToPictures.toClass
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object SaveToMovies : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SCREEN_RECORDER_SAVE_TO_MOVIES) {
            "android.os.Environment".toClass().field {
                name = "DIRECTORY_DCIM"
                modifiers { isStatic }
            }.get().set("Movies")
            "android.content.ContentValues".toClass()
                .method {
                    name = "put"
                    param(StringClass, StringClass)
                }
                .hook {
                    before {
                        if (this.args(0).string() == "relative_path") {
                            this.args(1).set(this.args(1).string().replace("DCIM", "Movies"))
                        }
                    }
                }
        }
    }
}