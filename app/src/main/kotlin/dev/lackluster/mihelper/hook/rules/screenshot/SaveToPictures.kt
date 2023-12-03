package dev.lackluster.mihelper.hook.rules.screenshot

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object SaveToPictures : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SCREENSHOT_SAVE_TO_PICTURE) {
            "android.os.Environment".toClass().field {
                name = "DIRECTORY_DCIM"
                modifiers { isStatic }
            }.get().set("Pictures")
        }
    }
}