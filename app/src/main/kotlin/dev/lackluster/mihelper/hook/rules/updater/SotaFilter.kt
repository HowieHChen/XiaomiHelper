package dev.lackluster.mihelper.hook.rules.updater

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.hook.utils.toTyped

object SotaFilter : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    @Volatile
    private var pkgBlacklist = emptyList<String>()

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.Updater.ENABLE_SOTA_UPDATE_FILTER.observe {
                updateSelfState(it)
            }
        )
    }

    override fun onStart() {
        registerStopCleanup(
            Preferences.Updater.SOTA_UPDATE_FILTER_PACKAGES.observe {
                pkgBlacklist = it.split(',', ' ', '，').filter { it1 -> it1.isNotBlank() }
            }
        )
        "com.android.updater.xms.bean.XmsUpdateInfo".toClassOrNull()?.apply {
            val fldApkLists = resolve().firstFieldOrNull {
                name = "pkgs"
            }?.toTyped<List<Any?>>()
            resolve().firstMethodOrNull {
                name = "getPkgs"
            }?.hook {
                val ori = proceed()
                val apkLists = fldApkLists?.get(thisObject) ?: ori as? List<Any?>
                val filteredList = apkLists?.filter {
                    it !in pkgBlacklist
                }?.toMutableList()
                fldApkLists?.set(thisObject, filteredList)
                result(filteredList)
            }
        }
    }
}