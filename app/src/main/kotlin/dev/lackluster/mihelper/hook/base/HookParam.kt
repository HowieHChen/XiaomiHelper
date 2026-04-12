package dev.lackluster.mihelper.hook.base

import android.content.pm.ApplicationInfo

data class HookParam(
    val processName: String,
    val packageName: String,
    val isSystemServer: Boolean,
    val isFirstPackage: Boolean = false,
    val isPackageReady: Boolean = false,
    val appInfo: ApplicationInfo? = null
) {
    val isMainProcess: Boolean
        get() = processName == packageName
}