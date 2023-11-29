package dev.lackluster.mihelper.hook.rules.miuihome

import android.view.MotionEvent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object AnimEnhance : YukiBaseHooker() {
    private var appToHomeAnim2Bak: Any? = null
    private var runnable = Runnable { }
    override fun onHook() {
        hasEnable(PrefKey.HOME_ANIM_ENHANCE) {
            "com.miui.home.recents.NavStubView".toClass()
                .method {
                    name = "onInputConsumerEvent"
                }.ignored()
                .hook {
                    before {
                        appToHomeAnim2Bak = this.instance.current().field {
                            name = "mAppToHomeAnim2"
                        }.any()
                        if (appToHomeAnim2Bak != null) {
                            this.instance.current().field {
                                name = "mAppToHomeAnim2"
                            }.setNull()
                        }
                    }
                    after {
                        try {
                            val motionEvent = this.args(0).cast<MotionEvent>() ?: return@after
                            // YLog.info("onInputConsumerEvent: Action: ${motionEvent.action}, return ${this.result}. x:${motionEvent.x} y:${motionEvent.y}")
                            if (this.instance.current().field {
                                name = "mAppToHomeAnim2"
                                }.any() != null || appToHomeAnim2Bak == null)
                                return@after
                            else {
                                this.instance.current().field {
                                    name = "mAppToHomeAnim2"
                                }.set(appToHomeAnim2Bak)
                            }
                        }
                        catch (tout: Throwable) {
                            YLog.info("Catch MotionEvent Failed!\n${tout}")
                        }
                    }
                }
            "com.miui.home.launcher.ItemIcon".toClass()
                .method {
                    name = "initPerformClickRunnable"
                }
                .hook {
                    before {
                        this.result = runnable
                    }
                }
        }
    }
}