package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object DisguiseNoNet : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.DISGUISE_NO_NETWORK) {
            "android.net.NetworkInfo".toClass().apply {
                method {
                    name = "isConnected"
                }.hook {
                    replaceToFalse()
                }
                method {
                    name = "isConnectedOrConnecting"
                }.hook {
                    replaceToFalse()
                }
            }
        }
    }
}