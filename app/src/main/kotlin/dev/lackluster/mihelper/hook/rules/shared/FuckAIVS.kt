package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import java.io.File

class FuckAIVS(val key: PreferenceKey<Boolean>) : StaticHooker() {
    override fun onInit() {
        updateSelfState(key.get())
    }

    override fun onHook() {
        "com.xiaomi.aivsbluetoothsdk.utils.FileUtil".toClassOrNull()?.apply {
            resolve().firstMethodOrNull {
                name = "splicingFilePath"
                parameters(String::class, String::class, String::class, String::class)
            }?.hook {
                val newArgs = args.toTypedArray()

                val rootDir = newArgs[0] as? String ?: ""
                val cleanRootDir = rootDir.trim { it == '/' || it == File.separatorChar }

                newArgs[0] = "MIUI${File.separator}AIVS${File.separator}${cleanRootDir}"
                
                result(proceed(newArgs))
            }
        }
    }
}