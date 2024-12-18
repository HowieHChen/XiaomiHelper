package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object HideRedDot : YukiBaseHooker() {
    private val sixCardViewHolderClass by lazy {
        DexKit.findClassWithCache("home_6card_vh") {
            matcher {
                addUsingString("FuncGrid6ViewHolder", StringMatchType.Equals)
                addUsingString("appmanager_red", StringMatchType.Equals)
            }
        }
    }
    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.HIDE_RED_DOT) {
            if (appClassLoader == null) return@hasEnable
            sixCardViewHolderClass?.getInstance(appClassLoader!!)?.apply {
                method {
                    name = "refreshAntiSpam"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshAppManager"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshCleanMaster"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshDeepClean"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshNetworkAssist"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshOptimizemanage"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshPowerCenter"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
                method {
                    name = "refreshSecurityScan"
                }.hook {
                    before {
                        this.args(0).setTrue()
                    }
                }
            }
        }
    }
}