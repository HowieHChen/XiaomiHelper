package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType
import kotlin.getValue

object HideRedDot : StaticHooker() {
    private val sixCardViewHolderClass by lazy {
        DexKit.findClassWithCache("home_6card_vh") {
            matcher {
                addUsingString("FuncGrid6ViewHolder", StringMatchType.Equals)
                addUsingString("appmanager_red", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        Preferences.SecurityCenter.HIDE_HOME_RED_DOT.get().also {
            updateSelfState(it)
        }.ifTrue {
            sixCardViewHolderClass
        }
    }

    override fun onHook() {
        sixCardViewHolderClass?.getInstance(classLoader)?.apply {
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