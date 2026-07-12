package dev.lackluster.mihelper.app.state

data class AppEnvState(
    val isModuleActivated: Boolean = false,
    val isModuleEnabled: Boolean = true,
    private val isRootGranted: Boolean = false,
    val isRootIgnored: Boolean = false,
    private val isSystemVersionSupported: Boolean = false,
    private val isSystemVersionWarningIgnored: Boolean = false,
) {
    val canWork: Boolean
        get() = isModuleActivated && isModuleEnabled && isRootRequirementSatisfied

    val isRootRequirementSatisfied: Boolean
        get() = isRootGranted || isRootIgnored

    val isSystemVersionRequirementSatisfied: Boolean
        get() = isSystemVersionSupported || isSystemVersionWarningIgnored
}
