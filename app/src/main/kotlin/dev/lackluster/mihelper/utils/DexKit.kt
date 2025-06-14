package dev.lackluster.mihelper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.Pref
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindField
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.wrap.DexClass
import org.luckypray.dexkit.wrap.DexField
import org.luckypray.dexkit.wrap.DexMethod
import java.io.File
import java.text.SimpleDateFormat


object DexKit {
    private const val FILE_NAME = "hyper_helper_dexkit"
    private const val KEY_HOST_VERSION = "ver__host"
    private const val KEY_MODULE_VERSION = "ver__module"
    private const val KEY_SYSTEM_VERSION = "ver__system"
    private const val PREF_KEY_CLASS_PREFIX = "clz_"
    private const val PREF_KEY_CLASSES_PREFIX = "cls_"
    private const val PREF_KEY_METHOD_PREFIX = "met_"
    private const val PREF_KEY_METHODS_PREFIX = "mes_"
    private const val PREF_KEY_FIELD_PREFIX = "fld_"
    private const val PREF_KEY_FIELDS_PREFIX = "fls_"
    private lateinit var hostDir: String
    private var enableCache = Prefs.getBoolean(Pref.Key.Module.DEX_KIT_CACHE, true)
    private var sp: SharedPreferences? = null
    private var isInitialized = false

    lateinit var dexKitBridge: DexKitBridge

    @SuppressLint("SdCardPath")
    fun initDexKit(param: PackageParam) {
        hostDir = param.appInfo.sourceDir
        if (enableCache) {
            try {
                val userDeSP = File("${param.appInfo.dataDir.replace("/data/user/", "/data/user_de/")}/shared_prefs/${FILE_NAME}.xml")
                sp = if (userDeSP.exists()) {
                    param.systemContext
                        .createPackageContext(param.packageName, Context.CONTEXT_IGNORE_SECURITY)
                        .createDeviceProtectedStorageContext()
                        .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                } else {
                    try {
                        param.systemContext
                            .createPackageContext(param.packageName, Context.CONTEXT_IGNORE_SECURITY)
                            .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                    } catch (_: Throwable) {
                        param.systemContext
                            .createPackageContext(param.packageName, Context.CONTEXT_IGNORE_SECURITY)
                            .createDeviceProtectedStorageContext()
                            .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                    }
                }
                val cachedHostVersion = sp?.getString(KEY_HOST_VERSION, "")
                val currentHostVersion = getPackageVersionString(param)
                val cachedModuleVersion = sp?.getString(KEY_MODULE_VERSION, "")
                val currentModuleVersion = BuildConfig.BUILD_TIME
                val cachedSystemVersion = sp?.getString(KEY_SYSTEM_VERSION, "")
                val currentSystemVersion = getSystemVersionString()
                if (
                    cachedHostVersion.isNullOrEmpty() || cachedHostVersion != currentHostVersion ||
                    cachedModuleVersion.isNullOrEmpty() || cachedModuleVersion != currentModuleVersion ||
                    cachedSystemVersion.isNullOrEmpty() || cachedSystemVersion != currentSystemVersion
                ) {
                    sp?.edit {
                        clear()
                        putString(KEY_HOST_VERSION, currentHostVersion)
                        putString(KEY_MODULE_VERSION, currentModuleVersion)
                        putString(KEY_SYSTEM_VERSION, currentSystemVersion)
                        apply()
                    }
                }
            } catch (t: Throwable) {
                YLog.warn("Cannot create cache file\n${t.message}")
                enableCache = false
            }
        } else {
            File("${param.appInfo.dataDir}/shared_prefs/${FILE_NAME}.xml").let {
                if (it.exists()) it.delete()
            }
            File("${param.appInfo.dataDir.replace("/data/user/", "/data/user_de/")}/shared_prefs/${FILE_NAME}.xml").let {
                if (it.exists()) it.delete()
            }
        }
        System.loadLibrary("dexkit")
        dexKitBridge = DexKitBridge.create(hostDir).also {
            isInitialized = true
        }
    }

    fun findClassWithCache(key: String, init: FindClass.() -> Unit): DexClass? {
        if (!enableCache) {
            return dexKitBridge.findClass(init).singleOrNull()?.toDexType()
        } else {
            val prefKey = "${PREF_KEY_CLASS_PREFIX}_${key}"
            val descriptor = sp?.getString(prefKey, null)
            descriptor?.let {
                return if (it == "null") null else DexClass(it)
            }
            val dexClass = dexKitBridge.findClass(init).singleOrNull()?.toDexType()
            sp?.edit {
                putString(prefKey, dexClass?.serialize() ?: "null")
                apply()
            }
            return dexClass
        }
    }

    fun findClassesWithCache(key: String, init: FindClass.() -> Unit): Set<DexClass> {
        if (!enableCache) {
            return dexKitBridge.findClass(init).map { it.toDexType() }.toSet()
        } else {
            val prefKey = "${PREF_KEY_CLASSES_PREFIX}_${key}"
            val descriptors = sp?.getStringSet(prefKey, null)
            descriptors?.let {
                return if (it.size == 1 && it.contains("null")) emptySet()
                else it.map { descriptor -> DexClass(descriptor) }.toSet()
            }
            val dexClasses = dexKitBridge.findClass(init).map { it.toDexType() }.toSet()
            sp?.edit {
                val dexClassesSet = dexClasses.map { dexClass -> dexClass.serialize() }.toSet()
                putStringSet(prefKey, dexClassesSet.takeIf { set -> set.isNotEmpty() } ?: setOf("null"))
            }
            return dexClasses
        }
    }

    fun findMethodWithCache(key: String, init: FindMethod.() -> Unit): DexMethod? {
        if (!enableCache) {
            return dexKitBridge.findMethod(init).singleOrNull()?.toDexMethod()
        } else {
            val prefKey = "${PREF_KEY_METHOD_PREFIX}_${key}"
            val descriptor = sp?.getString(prefKey, null)
            descriptor?.let {
                return if (it == "null") null else DexMethod(it)
            }
            val dexMethod = dexKitBridge.findMethod(init).singleOrNull()?.toDexMethod()
            sp?.edit {
                putString(prefKey, dexMethod?.serialize() ?: "null")
                apply()
            }
            return dexMethod
        }
    }

    fun findMethodsWithCache(key: String, init: FindMethod.() -> Unit): Set<DexMethod> {
        if (!enableCache) {
            return dexKitBridge.findMethod(init).map { it.toDexMethod() }.toSet()
        } else {
            val prefKey = "${PREF_KEY_METHODS_PREFIX}_${key}"
            val descriptors = sp?.getStringSet(prefKey, null)
            descriptors?.let {
                return if (it.size == 1 && it.contains("null")) emptySet()
                else it.map { descriptor -> DexMethod(descriptor) }.toSet()
            }
            val dexMethods = dexKitBridge.findMethod(init).map { it.toDexMethod() }.toSet()
            sp?.edit {
                val dexMethodsSet = dexMethods.map { dexMethod -> dexMethod.serialize() }.toSet()
                putStringSet(prefKey, dexMethodsSet.takeIf { set -> set.isNotEmpty() } ?: setOf("null"))
            }
            return dexMethods
        }
    }

    fun findFieldWithCache(key: String, init: FindField.() -> Unit): DexField? {
        if (!enableCache) {
            return dexKitBridge.findField(init).singleOrNull()?.toDexField()
        } else {
            val prefKey = "${PREF_KEY_FIELD_PREFIX}_${key}"
            val descriptor = sp?.getString(prefKey, null)
            descriptor?.let {
                return if (it == "null") null else DexField(it)
            }
            val dexField = dexKitBridge.findField(init).singleOrNull()?.toDexField()
            sp?.edit {
                putString(prefKey, dexField?.serialize() ?: "null")
                apply()
            }
            return dexField
        }
    }

    fun findFieldsWithCache(key: String, init: FindField.() -> Unit): Set<DexField> {
        if (!enableCache) {
            return dexKitBridge.findField(init).map { it.toDexField() }.toSet()
        } else {
            val prefKey = "${PREF_KEY_FIELDS_PREFIX}_${key}"
            val descriptors = sp?.getStringSet(prefKey, null)
            descriptors?.let {
                return if (it.size == 1 && it.contains("null")) emptySet()
                else it.map { descriptor -> DexField(descriptor) }.toSet()
            }
            val dexFields = dexKitBridge.findField(init).map { it.toDexField() }.toSet()
            sp?.edit {
                val dexMethodsSet = dexFields.map { dexField -> dexField.serialize() }.toSet()
                putStringSet(prefKey, dexMethodsSet.takeIf { set -> set.isNotEmpty() } ?: setOf("null"))
            }
            return dexFields
        }
    }

    fun closeDexKit() {
        if (isInitialized) dexKitBridge.close()
        sp = null
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPackageVersionString(param: PackageParam): String {
        param.let {
            try {
                val formatter = SimpleDateFormat("yyyyMMddHHmmss")
                val lastUpdatedTime = param.systemContext.packageManager.getPackageInfo(param.packageName, 0).lastUpdateTime
                return formatter.format(lastUpdatedTime)
//                val parserCls = "android.content.pm.PackageParser".toClass()
//                val parser = parserCls.getConstructor().newInstance()
//                val pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0)
//                val versionName = XposedHelpers.getObjectField(pkg, "mVersionName") as String
//                val versionCode = XposedHelpers.getObjectField(pkg, "mVersionCode") as Int
//                YLog.info("Dexkit versionName: $versionName versionCode: $versionCode")
//                return "${versionCode}_${versionName}$"
            } catch (_: Throwable) { }
        }
        return "null"
    }

    @SuppressLint("SimpleDateFormat")
    private fun getSystemVersionString(): String {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss")
        return formatter.format(Build.TIME)
    }
}