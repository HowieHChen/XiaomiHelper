package dev.lackluster.mihelper.hook.rules.milink

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.edit
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object EnhanceContinueTasks : YukiBaseHooker() {
    private const val APP_ID_SP = "hyper_helper_app_id"
    private val getAppInfoFromId by lazy {
        DexKit.findClassWithCache("ho_app_meta_provider") {
            matcher {
                addUsingString("AppMetaUtils", StringMatchType.Equals)
                addUsingString("appjson", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiMirror.ENHANCE_CONTINUE_TASKS, extraCondition = {
            Prefs.getBoolean(Pref.Key.MiMirror.CONTINUE_ALL_TASKS, false)
        }) {
            if (appClassLoader == null) return@hasEnable
            getAppInfoFromId?.getInstance(appClassLoader!!)?.apply {
                val pkgToIdMethod = method {
                    param(StringClass)
                    returnType = IntType
                }
                method {
                    param(IntType)
                    returnType = BundleClass
                }.hook {
                    after {
                        val result = this.result as? Bundle
                        if (result?.getString("androidPackageName", null) != null) return@after
                        val context = this.instance.current().field {
                            type = ContextClass
                            superClass()
                        }.cast<Context>() ?: return@after
                        val pm = context.packageManager
                        val id = this.args(0).int()
                        val sp = context.getSharedPreferences(APP_ID_SP, Context.MODE_PRIVATE)
                        if (sp.getString(id.toString(), null).isNullOrEmpty()) {
                            val map = mutableMapOf<String, String>()
                            pm.getInstalledApplications(ApplicationInfo.FLAG_HAS_CODE).forEach { info ->
                                info.packageName?.takeIf { it.isNotEmpty() }?.let { pkg ->
                                    val tmpId = pkgToIdMethod.get(this.instance).original().int(pkg)
                                    map[tmpId.toString()] = pkg
                                }
                            }
                            sp.edit {
                                for (kv in map) {
                                    putString(kv.key, kv.value)
                                }
                                commit()
                            }
                        }
                        val spPkgName = sp.getString(id.toString(), null)?.takeIf { it.isNotEmpty() } ?: return@after
                        val application = try {
                            pm.getApplicationInfo(spPkgName, 0)
                        } catch (_: PackageManager.NameNotFoundException) {
                            null
                        }
                        application?.let {
                            this.result = Bundle().apply {
                                putInt("appId", id)
                                putString("name", it.loadLabel(pm).toString())
                                putString("androidPackageName", spPkgName)
                                putString("iconUri", "pkg://${spPkgName}")
                            }
                        }
                    }
                }
                method {
                    modifiers { isPublic }
                    param(StringClass)
                    returnType = BundleClass
                }.hook {
                    after {
                        val result = this.result as? Bundle
                        if (result?.getString("androidPackageName", null) != null) return@after
                        val context = this.instance.current().field {
                            type = ContextClass
                            superClass()
                        }.cast<Context>() ?: return@after
                        val pkg = this.args(0).string().takeIf { it.isNotEmpty() } ?: return@after
                        val pm = context.packageManager
                        val sp = context.getSharedPreferences(APP_ID_SP, Context.MODE_PRIVATE)
                        val id = pkgToIdMethod.get(this.instance).original().int(pkg)
                        if (!sp.contains(id.toString())) {
                            sp.edit {
                                putString(id.toString(), pkg)
                                apply()
                            }
                        }
                        val application = try {
                            pm.getApplicationInfo(pkg, 0)
                        } catch (_: PackageManager.NameNotFoundException) {
                            null
                        }
                        application?.let {
                            this.result = Bundle().apply {
                                putInt("appId", id)
                                putString("name", it.loadLabel(pm).toString())
                                putString("androidPackageName", pkg)
                                putString("iconUri", "pkg://${pkg}")
                            }
                        }
                    }
                }
                pkgToIdMethod.hook {
                    after {
                        val pkg = this.args(0).string()
                        val id = this.result as? Int ?: return@after
                        val context = this.instance.current().field {
                            type = ContextClass
                            superClass()
                        }.cast<Context>() ?: return@after
                        context.getSharedPreferences(APP_ID_SP, Context.MODE_PRIVATE).edit {
                            putString(id.toString(), pkg)
                            apply()
                        }
                    }
                }
            }
        }
    }
}