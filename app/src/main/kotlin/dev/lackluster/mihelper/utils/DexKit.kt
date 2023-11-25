package dev.lackluster.mihelper.utils

import com.highcapable.yukihookapi.hook.param.PackageParam
import org.luckypray.dexkit.DexKitBridge

object DexKit {
    private lateinit var hostDir: String
    private var isInitialized = false
    val dexKitBridge: DexKitBridge by lazy {
        System.loadLibrary("dexkit")
        DexKitBridge.create(hostDir).also {
            isInitialized = true
        }
    }

    fun initDexKit(param: PackageParam) {
        hostDir = param.appInfo.sourceDir
    }

    fun closeDexKit() {
        if (isInitialized) dexKitBridge.close()
    }
}