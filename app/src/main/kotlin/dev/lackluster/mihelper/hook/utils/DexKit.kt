package dev.lackluster.mihelper.hook.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.core.content.edit
import dev.lackluster.mihelper.BuildConfig
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.HookParam
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.utils.MLog
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.FindClass
import org.luckypray.dexkit.query.FindField
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.wrap.DexClass
import org.luckypray.dexkit.wrap.DexField
import org.luckypray.dexkit.wrap.DexMethod
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

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

    private const val TAG = "DexKit"

    private var apkPath: String = ""
    private var dexKitBridge: DexKitBridge? = null
    private var enableCache: Boolean = false
    private var cacheSp: SharedPreferences? = null
    private var refCount = 0
    private var isInitialized: Boolean = false
    private var isReleased: Boolean = true



    fun retain(param: HookParam) {
        if (refCount == 0) {
            if (param.appInfo == null) return
            init(param.appInfo)
        }
        refCount++
    }

    private fun init(appInfo: ApplicationInfo) {
        if (isInitialized) return
        isInitialized = true

        apkPath = appInfo.sourceDir
        enableCache = Preferences.Module.DEX_KIT_CACHE.get()
        val dataDir = appInfo.dataDir
        if (enableCache) {
            try {
                val systemContext = systemContext() ?: error("Failed to got SystemContext")
                val targetContext = systemContext.createPackageContext(appInfo.packageName, Context.CONTEXT_IGNORE_SECURITY)
                cacheSp = try {
                    targetContext
                        .createDeviceProtectedStorageContext()
                        .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                } catch (_: Throwable) {
                    targetContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                }
                validate()
            } catch (t: Throwable) {
                enableCache = false
                MLog.e(TAG, t = t) { "Failed to init cacheSp" }
            }
        } else {
            cacheSpFile(dataDir, true).let { if (it.exists()) it.delete() }
            cacheSpFile(dataDir, false).let { if (it.exists()) it.delete() }
        }
        isReleased = false
        MLog.v(TAG) { "Successfully initialized" }
    }

    fun findClassWithCache(key: String, init: FindClass.() -> Unit): DexClass? {
        if (!enableCache) {
            return requireBridge().findClass(init).singleOrNull()?.toDexClass()
        }
        val prefKey = "${PREF_KEY_CLASS_PREFIX}_${key}"
        cacheSp?.getString(prefKey, null)?.let {
            return if (it == "null") null else DexClass(it)
        }
        val dexClass = requireBridge().findClass(init).singleOrNull()?.toDexClass()
        cacheSp?.edit {
            putString(prefKey, dexClass?.serialize() ?: "null")
        }
        return dexClass
    }

    fun findClassesWithCache(key: String, init: FindClass.() -> Unit): Set<DexClass> {
        if (!enableCache) {
            return requireBridge().findClass(init).map { it.toDexClass() }.toSet()
        }
        val prefKey = "${PREF_KEY_CLASSES_PREFIX}_${key}"
        cacheSp?.getStringSet(prefKey, null)?.let {
            return if (it.size == 1 && it.contains("null")) emptySet()
            else it.map { descriptor -> DexClass(descriptor) }.toSet()
        }
        val dexClasses = requireBridge().findClass(init).map { it.toDexClass() }.toSet()
        cacheSp?.edit {
            val dexClassesSet = dexClasses.map { it.serialize() }.toSet()
            putStringSet(prefKey, dexClassesSet.takeIf { set -> set.isNotEmpty() } ?: setOf("null"))
        }
        return dexClasses
    }

    fun findMethodWithCache(key: String, init: FindMethod.() -> Unit): DexMethod? {
        if (!enableCache) {
            return requireBridge().findMethod(init).singleOrNull()?.toDexMethod()
        }
        val prefKey = "${PREF_KEY_METHOD_PREFIX}_${key}"
        cacheSp?.getString(prefKey, null)?.let {
            return if (it == "null") null else DexMethod(it)
        }
        val dexMethod = requireBridge().findMethod(init).singleOrNull()?.toDexMethod()
        cacheSp?.edit {
            putString(prefKey, dexMethod?.serialize() ?: "null")
        }
        return dexMethod
    }

    fun findMethodsWithCache(key: String, init: FindMethod.() -> Unit): Set<DexMethod> {
        if (!enableCache) {
            return requireBridge().findMethod(init).map { it.toDexMethod() }.toSet()
        }
        val prefKey = "${PREF_KEY_METHODS_PREFIX}_${key}"
        cacheSp?.getStringSet(prefKey, null)?.let {
            return if (it.size == 1 && it.contains("null")) emptySet()
            else it.map { descriptor -> DexMethod(descriptor) }.toSet()
        }
        val dexMethods = requireBridge().findMethod(init).map { it.toDexMethod() }.toSet()
        cacheSp?.edit {
            val dexMethodsSet = dexMethods.map { it.serialize() }.toSet()
            putStringSet(prefKey, dexMethodsSet.takeIf { set -> set.isNotEmpty() } ?: setOf("null"))
        }
        return dexMethods
    }

    fun findFieldWithCache(key: String, init: FindField.() -> Unit): DexField? {
        if (!enableCache) {
            return requireBridge().findField(init).singleOrNull()?.toDexField()
        }
        val prefKey = "${PREF_KEY_FIELD_PREFIX}_${key}"
        cacheSp?.getString(prefKey, null)?.let {
            return if (it == "null") null else DexField(it)
        }
        val dexField = requireBridge().findField(init).singleOrNull()?.toDexField()
        cacheSp?.edit {
            putString(prefKey, dexField?.serialize() ?: "null")
        }
        return dexField
    }

    fun findFieldsWithCache(key: String, init: FindField.() -> Unit): Set<DexField> {
        if (!enableCache) {
            return requireBridge().findField(init).map { it.toDexField() }.toSet()
        }
        val prefKey = "${PREF_KEY_FIELDS_PREFIX}_${key}"
        val descriptors = cacheSp?.getStringSet(prefKey, null)
        descriptors?.let {
            return if (it.size == 1 && it.contains("null")) emptySet()
            else it.map { descriptor -> DexField(descriptor) }.toSet()
        }
        val dexFields = requireBridge().findField(init).map { it.toDexField() }.toSet()
        cacheSp?.edit {
            val dexMethodsSet = dexFields.map { it.serialize() }.toSet()
            putStringSet(prefKey, dexMethodsSet.takeIf { set -> set.isNotEmpty() } ?: setOf("null"))
        }
        return dexFields
    }

    fun <T> withBridge(action: DexKitBridge.() -> T): T {
        return requireBridge().action()
    }

    fun release() {
        if (refCount == 0) return

        refCount--

        if (refCount == 0) {
            isReleased = true
            dexKitBridge?.close()
            dexKitBridge = null
            isInitialized = false
            MLog.v(TAG) { "Released" }
        }
    }

    private fun requireBridge(): DexKitBridge {
        if (isReleased) {
            throw IllegalStateException("DexKit 已经被释放！严禁在运行时动态查找特征！")
        }
        if (dexKitBridge == null) {
            MLog.v(TAG) { "Cache miss! Loading DexKit C++ library into memory..." }
            System.loadLibrary("dexkit")
            dexKitBridge = DexKitBridge.create(apkPath)
        }
        return dexKitBridge!!
    }

    @SuppressLint("PrivateApi")
    private fun systemContext(): Context? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            activityThreadClass.getMethod("getSystemContext").invoke(currentActivityThread) as Context?
        } catch (_: Exception) {
            null
        }
    }

    @SuppressLint("SdCardPath")
    private fun cacheSpFile(dataDir: String, deviceProtect: Boolean = false): File {
        return if (deviceProtect) {
            File("${dataDir.replace("/data/user/", "/data/user_de/")}/shared_prefs/${FILE_NAME}.xml")
        } else {
            File("${dataDir}/shared_prefs/${FILE_NAME}.xml")
        }
    }

    private fun validate() {
        val sp = cacheSp ?: return

        val cachedHostVersion = sp.getString(KEY_HOST_VERSION, "")
        val currentHostVersion = getPackageVersionString()
        val cachedModuleVersion = sp.getString(KEY_MODULE_VERSION, "")
        val currentModuleVersion = BuildConfig.BUILD_TIME
        val cachedSystemVersion = sp.getString(KEY_SYSTEM_VERSION, "")
        val currentSystemVersion = getSystemVersionString()

        if (
            cachedHostVersion.isNullOrEmpty() || cachedHostVersion != currentHostVersion ||
            cachedModuleVersion.isNullOrEmpty() || cachedModuleVersion != currentModuleVersion ||
            cachedSystemVersion.isNullOrEmpty() || cachedSystemVersion != currentSystemVersion
        ) {
            sp.edit {
                clear()
                putString(KEY_HOST_VERSION, currentHostVersion)
                putString(KEY_MODULE_VERSION, currentModuleVersion)
                putString(KEY_SYSTEM_VERSION, currentSystemVersion)
            }
        }
    }

    private fun getPackageVersionString(): String {
        try {
            val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            val lastUpdatedTime = File(apkPath).lastModified()
            return formatter.format(lastUpdatedTime)
        } catch (_: Throwable) {
            return "null"
        }
    }

    private fun getSystemVersionString(): String {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        return formatter.format(Build.TIME)
    }
}