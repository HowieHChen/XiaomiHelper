package dev.lackluster.mihelper.hook.rules.systemui.media

import android.content.Context
import android.content.res.Configuration
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMiuiMediaViewControllerImpl
import dev.lackluster.mihelper.utils.Prefs

object AlwaysDark : YukiBaseHooker() {
    private val ncBackgroundStyle = Prefs.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    private val ncAlwaysDark = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.ALWAYS_DARK, false)
    private val ncAmbientLight = Prefs.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT, false)

    override fun onHook() {
        if (ncBackgroundStyle == 0 && (ncAlwaysDark || ncAmbientLight)) {
            clzMiuiMediaViewControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "context"
                }?.self?.apply {
                    isAccessible = true
                }
                val fldMediaFullAodListener = resolve().firstFieldOrNull {
                    name = "mediaFullAodListener"
                }?.self
                val fldFullAodController = resolve().firstFieldOrNull {
                    name = "fullAodController"
                }?.self
                val fldListeners = "com.android.systemui.statusbar.notification.fullaod.NotifiFullAodController".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mListeners"
                    }?.self
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.self
                resolve().firstConstructor().hook {
                    after {
                        val context = fldContext?.get(this.instance) as? Context ?: return@after
                        val oriConfiguration = context.resources.configuration
                        val configuration = Configuration(oriConfiguration).apply {
                            uiMode = (oriConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                        }
                        val wrappedContext = context.createConfigurationContext(configuration)
                        fldContext.set(this.instance, wrappedContext)
                    }
                }
                resolve().firstMethodOrNull {
                    name = "attach"
                }?.hook {
                    after {
                        val mediaFullAodListener = fldMediaFullAodListener?.get(this.instance) ?: return@after
                        val fullAodControllerLazy = fldFullAodController?.get(this.instance)
                        val fullAodController = fullAodControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                        fullAodController?.let { it1 -> fldListeners?.get(it1) as? MutableList<*> }?.remove(mediaFullAodListener)
                    }
                }
            }
        }
    }
}