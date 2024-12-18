package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.ListClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object HideHomeElement : YukiBaseHooker() {
    private val hideRec = Prefs.getBoolean(Pref.Key.SecurityCenter.HIDE_HOME_REC, false)
    private val hideCommon = Prefs.getBoolean(Pref.Key.SecurityCenter.HIDE_HOME_COMMON, false)
    private val hidePopular = Prefs.getBoolean(Pref.Key.SecurityCenter.HIDE_HOME_POPULAR, false)
    private val removeElements by lazy {
        mutableListOf<String>().apply {
            if (hideRec) {
                add("com.miui.common.card.models.FuncListBannerCardModel")
            }
            if (hideCommon) {
                add("com.miui.common.card.models.CommonlyUsedFunctionCardModel")
                add("com.miui.common.card.models.CommonlyUsedFunctionCardModelNew")
                add("com.miui.common.card.models.CommonlyUsedFunctionCardTitleModel")
            }
            if (hidePopular) {
                add("com.miui.common.card.models.PopularActionCardModel")
            }
        }
    }

    override fun onHook() {
        if (hideRec || hideCommon || hidePopular) {
            "com.miui.common.card.CardViewRvAdapter".toClassOrNull()?.apply {
                method {
                    name = "addAll"
                    param(ListClass)
                }.hook {
                    before {
                        val filteredList = this.args(0).list<Any>().filterNot {
                            removeElements.contains(it.javaClass.name)
                        }.toList()
                        this.args(0).set(filteredList)
                    }
                }
                method {
                    name = "setModelList"
                    param(ArrayListClass)
                }.hook {
                    before {
                        val filteredList = this.args(0).list<Any>().filterNot {
                            removeElements.contains(it.javaClass.name)
                        }.toMutableList()
                        this.args(0).set(filteredList)
                    }
                }
            }
        }
    }
}