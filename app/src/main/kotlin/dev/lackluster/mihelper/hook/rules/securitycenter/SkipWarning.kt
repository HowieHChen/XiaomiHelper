package dev.lackluster.mihelper.hook.rules.securitycenter

import android.os.Handler
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object SkipWarning : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_SKIP_WARNING) {
            "android.widget.TextView".toClass()
                .method {
                    name = "setEnabled"
                    param(BooleanType)
                }.hook {
                    before {
                        this.args(0).set(true)
                    }
                }
            try {
                val mInnerClasses = "com.miui.permcenter.privacymanager.InterceptBaseFragment".toClass().declaredClasses
                var mHandlerClass: Class<*>? = null
                for (mInnerClass in mInnerClasses) {
                    if (Handler::class.java.isAssignableFrom(mInnerClass)) {
                        mHandlerClass = mInnerClass
                        break
                    }
                }
                if (mHandlerClass != null) {
                    mHandlerClass
                        .constructor()
                        .hookAll {
                            before {
                                if (this.args.size == 2) {
                                    this.args(1).set(0)
                                }
                            }
                        }
                    mHandlerClass
                        .method {
                            returnType = Void.TYPE
                            param(IntType)
                        }
                        .ignored()
                        .hook {
                            before {
                                this.args(0).set(0)
                            }
                        }
                }
            }
            catch (_: Throwable) {
                YLog.info("Failed to find class: com.miui.permcenter.privacymanager.InterceptBaseFragment")
            }
        }
    }
}