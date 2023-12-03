package dev.lackluster.mihelper.hook.rules.download

import android.os.Environment
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import java.io.File
import java.io.FileNotFoundException

object RemoveXLDownload : YukiBaseHooker() {
    private val targetAbsPath by lazy {
        File(Environment.getExternalStorageDirectory(), ".xlDownload").absoluteFile
    }

    override fun onHook() {
        hasEnable(PrefKey.DOWNLOAD_REMOVE_XL) {
            "com.android.providers.downloads.config.XLConfig".toClass().apply {
                method {
                    name = "setDebug"
                }.hook {
                    before {
                        this.result = null
                    }
                }
                method {
                    name = "setSoDebug"
                }.hook {
                    before {
                        this.result = null
                    }
                }
            }
//            File::class.java
//                .method {
//                    name = "mkdirs"
//                }
//                .hook {
//                    before {
//                        if ((this.instance as File).absoluteFile.equals(targetAbsPath)) {
//                            FileNotFoundException("blocked").throwToApp()
//                        }
//                    }
//                }
        }
    }
}