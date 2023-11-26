package dev.lackluster.mihelper.hook.rules.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object ShowGoogle : YukiBaseHooker() {
    private val isGlobal by lazy {
        "miui.os.Build".toClass().field {
            name = "IS_GLOBAL_BUILD"
        }.get().boolean()
    }
    override fun onHook() {
        hasEnable(PrefKey.SETTINGS_SHOW_GOOGLE, extraCondition = { !isGlobal }) {
            "com.android.settings.MiuiSettings".toClass()
                .method {
                    name = "updateHeaderList"
                }
                .hook {
                    after {
                        val list = this.args(0).any()
                        this.instance.current {
                            method {
                                name = "AddGoogleSettingsHeaders"
                            }.call(list)
                        }
                    }
                }
        }
    }
}