package dev.lackluster.mihelper.hook.rules.packageinstaller

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object DisableSafeModelTip : YukiBaseHooker() {
    private val miuiSettingsAd by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("android.provider.MiuiSettings\$Ad", StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_SAFE_MODE_TIP) {
            runCatching {
                miuiSettingsAd?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                    replaceToFalse()
                }
                "com.miui.packageInstaller.model.ApkInfo".toClassOrNull()?.method {
                    name = "getSystemApp"
                }?.hook {
                    replaceToTrue()
                }
                "com.miui.packageInstaller.InstallProgressActivity".toClassOrNull()?.method {
                    name = "g0"
                }?.ignored()?.hook {
                    replaceToFalse()
                }
                "com.miui.packageInstaller.InstallProgressActivity".toClassOrNull()?.method {
                    name = "Q1"
                }?.ignored()?.hook {
                    before {
                        this.result = null
                    }
                }
                "com.miui.packageInstaller.InstallProgressActivity".toClassOrNull()?.method()?.hookAll {
                    after {
                        this.instance.javaClass.field {
                            type = BooleanType
                        }.ignored().all(this.instance).firstOrNull()?.setFalse()
                    }
                }
            }
        }
    }
}