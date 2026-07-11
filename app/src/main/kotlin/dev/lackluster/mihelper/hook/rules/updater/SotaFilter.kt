package dev.lackluster.mihelper.hook.rules.updater

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.toTyped

object SotaFilter : StaticHooker() {
    private val pkgBlacklist by lazy {
        Preferences.Updater.SOTA_UPDATE_FILTER_PACKAGES.get().split(',', ' ', '，').filter { it.isNotBlank() }
    }

    override fun onInit() {
        updateSelfState(Preferences.Updater.ENABLE_SOTA_UPDATE_FILTER.get())
    }

    override fun onHook() {
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