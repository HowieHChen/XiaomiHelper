package dev.lackluster.mihelper.hook.rules.browser

import android.view.View
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import org.luckypray.dexkit.query.enums.UsingType
import java.lang.reflect.Modifier

object HideAIModeButton : YukiBaseHooker() {
    private val homePageClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("homepage_switch_bubble_expose", StringMatchType.Equals)
                addUsingString("hot_card_tag_click", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val aiEntranceButtonField by lazy {
        if (homePageClass == null) null
        else DexKit.findFieldWithCache("ai_entrance_btn") {
            matcher {
                declaredClass(homePageClass!!.name, StringMatchType.Equals)
                type = "android.widget.ImageView"
                modifiers(Modifier.PUBLIC or Modifier.FINAL)
            }
            searchClasses = homePageClass?.let { listOf(it) }
        }
    }
    private val updateButtonVisibleMethod by lazy {
        if (aiEntranceButtonField == null) null
        else DexKit.findMethodWithCache("ai_entrance_visible") {
            matcher {
                addUsingField(aiEntranceButtonField!!.toString(), UsingType.Read)
            }
            searchClasses = homePageClass?.let { listOf(it) }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Browser.HIDE_AI_MODE_BUTTON) {
            if (appClassLoader == null) return@hasEnable
            val imageView = aiEntranceButtonField?.getFieldInstance(appClassLoader!!)
            updateButtonVisibleMethod?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    (imageView?.get(this.instance) as? ImageView)?.visibility = View.GONE
                }
            }
        }
    }
}