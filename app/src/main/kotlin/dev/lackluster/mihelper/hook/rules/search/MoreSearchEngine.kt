package dev.lackluster.mihelper.hook.rules.search

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.model.SearchEngineItem
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object MoreSearchEngine : StaticHooker() {
    private val customSearchEngine by Preferences.Search.ENABLE_CUSTOM_SEARCH_ENGINE.lazyGet()
    private val clzSearchEngineItem by lazy {
        DexKit.findClassWithCache("search_engine_item") {
            matcher {
                addUsingString("SearchEngineItem{searchEngineName=\'", StringMatchType.Equals)
            }
        }
    }
    private val clzSearchEngineSet by lazy {
        DexKit.findClassWithCache("search_engine_set") {
            matcher {
                addUsingString("SearchEngineSet{searchBox=",StringMatchType.Equals)
            }
        }
    }
    private val ctorSearchEngineItem by lazy {
        clzSearchEngineItem?.getInstance(classLoader)?.resolve()?.firstConstructor {
            parameterCount = 10
        }?.toTyped()
    }
    private val searchEngineItemBing by lazy {
        SearchEngineItem.Bing.toHostSearchEngineItem()
    }
    private val searchEngineItemGoogle by lazy {
        SearchEngineItem.Google.toHostSearchEngineItem()
    }
    private val searchEngineItemCustom by lazy {
        Preferences.Search.CUSTOM_SEARCH_ENGINE_ENTITY.get().takeIf {
            it.isNotEmpty()
        }?.let {
            SearchEngineItem.decodeFromString(it)
        }?.toHostSearchEngineItem()
    }

    override fun onInit() {
        Preferences.Search.MORE_SEARCH_ENGINE.get().also {
            updateSelfState(it)
        }.ifTrue {
            clzSearchEngineItem
            clzSearchEngineSet
        }
    }

    override fun onHook() {
        clzSearchEngineSet?.getInstance(classLoader)?.apply {
            resolve().firstConstructorOrNull {
                parameterCount = 5
            }?.hook {
                val searchBox = getArg(0)
                val hotRank = getArg(1)
                val addCustomEngine = customSearchEngine && searchEngineItemCustom != null
                @Suppress("UNCHECKED_CAST")
                val sceneSearchEngineMap = getArg(3) as LinkedHashMap<String, LinkedHashMap<String, Any?>>
                sceneSearchEngineMap["globalSearchSearchBox"]?.put("bing", searchEngineItemBing)
                sceneSearchEngineMap["globalSearchSearchBox"]?.put("google", searchEngineItemGoogle)
                sceneSearchEngineMap["globalSearchHotList"]?.put("bing", searchEngineItemBing)
                sceneSearchEngineMap["globalSearchHotList"]?.put("google", searchEngineItemGoogle)
                if (addCustomEngine) {
                    sceneSearchEngineMap["globalSearchSearchBox"]?.put("custom", searchEngineItemCustom)
                    sceneSearchEngineMap["globalSearchHotList"]?.put("custom", searchEngineItemCustom)
                }
                listOf(searchBox, hotRank).forEach {
                    val mapFiled = it.asResolver().firstField { type = "java.util.LinkedHashMap" }
                    val map = mapFiled.get<LinkedHashMap<String, Any?>>()?.apply {
                        put("bing", searchEngineItemBing)
                        put("google", searchEngineItemGoogle)
                        if (addCustomEngine) {
                            put("custom", searchEngineItemCustom)
                        }
                    }
                    map?.let { it1 -> mapFiled.set(it1) }
                    val listFiled = it.asResolver().firstField {  type = "java.util.List" }
                    val list = (listFiled.get<List<Any?>>())?.toMutableList()?.apply {
                        add(searchEngineItemBing)
                        add(searchEngineItemGoogle)
                        if (addCustomEngine) {
                            add(searchEngineItemCustom)
                        }
                    }
                    list?.let { it1 -> listFiled.set(it1) }
                    it.asResolver().field { type = Int::class }.minByOrNull { fld ->
                        fld.self.name
                    }?.let { count ->
                        count.set((count.get<Int>() ?: 0) + if (addCustomEngine) 3 else 2)
                    }
                }
                result(proceed())
            }
        }
    }

    private fun SearchEngineItem.toHostSearchEngineItem(): Any? {
        return ctorSearchEngineItem?.newInstance(
            searchEngineName,
            channelNo,
            showIcon,
            searchUrl,
            iconUrl,
            titleLzhCN,
            titleLzhTW,
            titleLenUS,
            titleLboCN,
            titleLugCN
        )
    }
}