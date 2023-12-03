package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object UnlimitedCropping : YukiBaseHooker() {
    private val mScreenCropViewMethodToNew by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addUsingString("not in bound", StringMatchType.Equals)
                }
                usingNumbers(0.5f, 200)
                returnType = "int"
                modifiers = Modifier.FINAL
            }
        }
    }
    private val mScreenCropViewMethodToOld by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addUsingString("fixImageBounds %f,%f", StringMatchType.Equals)
                }
                usingNumbers(0.5f, 200)
                returnType = "int"
            }
        }
    }

    override fun onHook() {
        hasEnable(PrefKey.SCREENSHOT_UNLIMITED_CROP) {
            if (mScreenCropViewMethodToNew.isNotEmpty()) {
                val newMethods = mScreenCropViewMethodToNew.map { it.getMethodInstance(appClassLoader ?: return@hasEnable) }.toList()
                newMethods.hookAll {
                    replaceTo(0)
                }
            }
            val oldMethod = mScreenCropViewMethodToOld.firstOrNull()?.getMethodInstance(appClassLoader ?: return@hasEnable)
            oldMethod?.hook {
                replaceTo(0)
            }
        }

    }
}