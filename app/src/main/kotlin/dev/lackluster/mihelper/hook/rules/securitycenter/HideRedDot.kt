package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe

object HideRedDot : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private val clzFuncGrid6ViewHolder by $$"com.miui.common.card.models.FuncGrid6CardModel$FuncGrid6ViewHolder".lazyClassOrNull()
//    private val sixCardViewHolderClass by lazy {
//        DexKit.findClassWithCache("home_6card_vh") {
//            matcher {
//                addUsingString("FuncGrid6ViewHolder", StringMatchType.Equals)
//                addUsingString("appmanager_red", StringMatchType.Equals)
//            }
//        }
//    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.SecurityCenter.HIDE_HOME_RED_DOT.observe {
                updateSelfState(it)
            }
        )
    }

    override fun onStart() {
        clzFuncGrid6ViewHolder?.apply {
            listOf(
                "refreshAntiSpam",
                "refreshAppManager",
                "refreshCleanMaster",
                "refreshDeepClean",
                "refreshNetworkAssist",
                "refreshOptimizemanage",
                "refreshPowerCenter",
                "refreshSecurityScan",
            ).forEach { metName ->
                resolve().firstMethodOrNull {
                    name = metName
                }?.hook {
                    val newArgs = args.toTypedArray()
                    newArgs[0] = true
                    result(proceed(newArgs))
                }
            }
        }
    }
}