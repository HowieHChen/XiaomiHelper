package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.factory.getResID

object ResourcesUtils : YukiBaseHooker() {
    private const val PKG_NAME = Scope.PACKAGE_INSTALLER
    private var isInitialized = false
    var feedback = 0
    var dialog_install_source = 0
    override fun onHook() {
        onAppLifecycle {
            onCreate {
                if (!isInitialized) {
                    if (this.resources == null) return@onCreate
                    feedback = this.getResID("feedback", "id", PKG_NAME)
                    dialog_install_source = this.getResID("dialog_install_source", "string", PKG_NAME)
                    isInitialized = true
                }
            }
        }
    }
}