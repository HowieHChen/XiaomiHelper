package dev.lackluster.mihelper.app.state

data class XposedState(
    val apiVersion: Int = 0,
    val frameworkName: String = "Unactivated",
    val frameworkVersion: String = "Unknown",
    val frameworkVersionCode: Long = 0L,
    val frameworkProperties: Long = 0L,
) {
    val versionInfo: String
        get() = "API${apiVersion}-${frameworkName}-${frameworkVersion}(${frameworkVersionCode})"
}
