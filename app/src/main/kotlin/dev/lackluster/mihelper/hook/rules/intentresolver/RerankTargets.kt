package dev.lackluster.mihelper.hook.rules.intentresolver

import android.content.pm.ResolveInfo
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.e
import dev.lackluster.mihelper.hook.utils.toTyped

object RerankTargets : StaticHooker() {
    private val rerankMode by Preferences.MiIntentResolver.RERANK_TARGETS.lazyGet()
    private val strict by Preferences.MiIntentResolver.RERANK_TARGETS_STRICT.lazyGet()

    private val pkgPositionMap by lazy {
        Preferences.MiIntentResolver.TARGETS_PKG_INDEX_MAP.get().associate {
            val parts = it.split(":", limit = 2)
            val pkgName = parts.getOrNull(0) ?: ""
            val index = parts[1].toIntOrNull() ?: Int.MAX_VALUE
            pkgName to index
        }
    }

    override fun onInit() {
        updateSelfState(rerankMode != 0)
    }

    override fun onHook() {
        $$"com.android.intentresolver.adapter.MiuiChooserGridAdapter$UpdateListTask".toClassOrNull()?.apply {
            if (rerankMode == 2) {
                resolve().firstMethodOrNull {
                    name = "checkAppInstalled"
                }?.hook {
                    result(false)
                }
            }
            val fldResolveInfo = "com.android.intentresolver.chooser.DisplayResolveInfo".toClassOrNull()?.resolve()?.firstFieldOrNull {
                name = "mResolveInfo"
                type(ResolveInfo::class)
            }?.toTyped<ResolveInfo>()
            resolve().firstMethodOrNull {
                name = "updateList"
            }?.hook {
                val list = proceed() as? List<*>
                if (list.isNullOrEmpty()) {
                    return@hook result(list)
                }
                val filteredList = list.filter { item ->
                    if (item == null) return@filter false
                    val pkg = try {
                        fldResolveInfo?.get(item)?.activityInfo?.packageName ?: ""
                    } catch (t: Throwable) {
                        e(t) { "Exception while getting packageName for $item" }
                        ""
                    }
                    (pkgPositionMap[pkg] ?: Int.MAX_VALUE) >= 0
                }
                if (strict) {
                    val fixedItems = mutableMapOf<Int, Any>()
                    val floatingItems = mutableListOf<Any>()
                    for (item in filteredList) {
                        if (item == null) continue
                        val pkg = try {
                            fldResolveInfo?.get(item)?.activityInfo?.packageName ?: ""
                        } catch (t: Throwable) {
                            e(t) { "Exception while getting packageName for $item" }
                            ""
                        }
                        val targetIndex = pkgPositionMap[pkg]
                        if (targetIndex != null && targetIndex >= 0 && !fixedItems.containsKey(targetIndex)) {
                            fixedItems[targetIndex] = item
                        } else {
                            floatingItems.add(item)
                        }
                    }
                    val maxConfiguredIndex = fixedItems.keys.maxOrNull() ?: -1
                    val virtualSize = maxOf(filteredList.size, maxConfiguredIndex + 1)
                    val virtualSlots = arrayOfNulls<Any>(virtualSize)

                    for ((index, item) in fixedItems) {
                        virtualSlots[index] = item
                    }

                    var floatingIterator = 0
                    for (i in virtualSlots.indices) {
                        if (virtualSlots[i] == null && floatingIterator < floatingItems.size) {
                            virtualSlots[i] = floatingItems[floatingIterator]
                            floatingIterator++
                        }
                    }

                    result(virtualSlots.filterNotNull().toMutableList())
                } else {
                    val sortedList = filteredList.sortedBy {
                        val pkg = try {
                            it?.let { it1 -> fldResolveInfo?.get(it1)?.activityInfo?.packageName } ?: ""
                        } catch (t: Throwable) {
                            e(t) { "Exception while getting packageName for $it" }
                            ""
                        }
                        pkgPositionMap[pkg] ?: Int.MAX_VALUE
                    }
                    result(sortedList.toMutableList())
                }
            }
        }
    }
}