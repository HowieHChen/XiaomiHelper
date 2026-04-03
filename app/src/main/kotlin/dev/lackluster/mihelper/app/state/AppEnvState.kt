package dev.lackluster.mihelper.app.state

data class AppEnvState(
    val isModuleActivated: Boolean = false,
    val isModuleEnabled: Boolean = true,
    val isRootGranted: Boolean = false,
    val isRootIgnored: Boolean = false
) {
    val canWork: Boolean
        get() = isModuleActivated && isModuleEnabled && (isRootGranted || isRootIgnored)
}
