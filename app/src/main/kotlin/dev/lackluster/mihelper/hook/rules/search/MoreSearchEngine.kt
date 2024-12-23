package dev.lackluster.mihelper.hook.rules.search

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object MoreSearchEngine : YukiBaseHooker() {
    private val customSearchEngine = Prefs.getBoolean(Pref.Key.Search.CUSTOM_SEARCH_ENGINE, false)
    private val searchEngineItemClass by lazy {
        DexKit.findClassWithCache("search_engine_item") {
            matcher {
                addUsingString("SearchEngineItem{searchEngineName=\'", StringMatchType.Equals)
            }
        }
    }
    private val searchEngineSetClass by lazy {
        DexKit.findClassWithCache("search_engine_set") {
            matcher {
                addUsingString("SearchEngineSet{searchBox=",StringMatchType.Equals)
            }
        }
    }
    private val searchEngineItem by lazy {
        searchEngineItemClass?.getInstance(appClassLoader!!)?.constructor{
            paramCount = 10
        }?.get()
    }
    private val searchEngineItemBing by lazy {
        SearchEngineItem.Bing.let {
            searchEngineItem?.call(
                it.searchEngineName,
                it.channelNo,
                it.showIcon,
                it.searchUrl,
                it.iconUrl,
                it.titleLzhCN,
                it.titleLzhTW,
                it.titleLenUS,
                it.titleLboCN,
                it.titleLugCN
            )
        }
    }
    private val searchEngineItemGoogle by lazy {
        SearchEngineItem.Google.let {
            searchEngineItem?.call(
                it.searchEngineName,
                it.channelNo,
                it.showIcon,
                it.searchUrl,
                it.iconUrl,
                it.titleLzhCN,
                it.titleLzhTW,
                it.titleLenUS,
                it.titleLboCN,
                it.titleLugCN
            )
        }
    }
    private val searchEngineItemCustom by lazy {
        Prefs.getString(Pref.Key.Search.CUSTOM_SEARCH_ENGINE_ENTITY, "")?.takeIf {
            it.isNotEmpty()
        }?.let {
            SearchEngineItem.decodeFromString(it)
        }?.let {
            searchEngineItem?.call(
                it.searchEngineName,
                it.channelNo,
                it.showIcon,
                it.searchUrl,
                it.iconUrl,
                it.titleLzhCN,
                it.titleLzhTW,
                it.titleLenUS,
                it.titleLboCN,
                it.titleLugCN
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onHook() {
        hasEnable(Pref.Key.Search.MORE_SEARCH_ENGINE) {
            if (appClassLoader == null || searchEngineItem == null) return@hasEnable
            searchEngineSetClass?.getInstance(appClassLoader!!)?.apply {
                constructor {
                    paramCount = 5
                }.hook {
                    before {
                        val searchBox = this.args(0).any() ?: return@before
                        val hotRank = this.args(1).any() ?: return@before
                        val addCustomEngine = customSearchEngine && searchEngineItemCustom != null
                        // val defaultSearchEngineMap = this.args(2).any() as LinkedHashMap<String, String>
                        val sceneSearchEngineMap = this.args(3).any() as LinkedHashMap<String, LinkedHashMap<String, Any?>>
                        // val e = this.args(4).any() as LinkedHashMap<String, Any?>
                        sceneSearchEngineMap["globalSearchSearchBox"]?.put("bing", searchEngineItemBing)
                        sceneSearchEngineMap["globalSearchSearchBox"]?.put("google", searchEngineItemGoogle)
                        sceneSearchEngineMap["globalSearchHotList"]?.put("bing", searchEngineItemBing)
                        sceneSearchEngineMap["globalSearchHotList"]?.put("google", searchEngineItemGoogle)
                        if (addCustomEngine) {
                            sceneSearchEngineMap["globalSearchSearchBox"]?.put("custom", searchEngineItemCustom)
                            sceneSearchEngineMap["globalSearchHotList"]?.put("custom", searchEngineItemCustom)
                        }
                        listOf(searchBox, hotRank).forEach {
                            val mapFiled = it.current().field { type = "java.util.LinkedHashMap" }
                            val map = (mapFiled.any() as? LinkedHashMap<String, Any?>)?.apply {
                                put("bing", searchEngineItemBing)
                                put("google", searchEngineItemGoogle)
                                if (addCustomEngine) {
                                    put("custom", searchEngineItemCustom)
                                }
                            }
                            map?.let { it1 -> mapFiled.set(it1) }
                            val listFiled = it.current().field {  type = ListClass }
                            val list = (listFiled.any() as? List<Any?>)?.toMutableList()?.apply {
                                add(searchEngineItemBing)
                                add(searchEngineItemGoogle)
                                if (addCustomEngine) {
                                    add(searchEngineItemCustom)
                                }
                            }
                            list?.let { it1 -> listFiled.set(it1) }
                            it.current().field { type = IntType }.let { count ->
                                count.set(count.int() + if (addCustomEngine) 3 else 2)
                            }
                        }
                    }
                }
            }

        }
    }
}