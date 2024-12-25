package dev.lackluster.mihelper.hook.rules.weather

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import org.luckypray.dexkit.query.enums.StringMatchType

object CardColor : YukiBaseHooker() {
    private val cardColor = Prefs.getInt(Pref.Key.Weather.CARD_COLOR, 0)
    private val mLightDarkModeValue by lazy {
        when (cardColor) {
            1 -> 2
            2 -> 3
            else -> 0
        }
    }
    private val oneCityBgColorMgrClass by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("Wth2:OneCityBackgroundColorManager", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    private val colorModeMethod by lazy {
        DexKit.findMethodWithCache("judge_color_mode") {
            matcher {
                addUsingString("judgeCurrentColor() mLightDarkMode : ", StringMatchType.Equals)
            }
            searchClasses = oneCityBgColorMgrClass?.let { listOf(it) }
        }
    }
    private val mLightDarkModeField by lazy {
        if (oneCityBgColorMgrClass == null || colorModeMethod == null) null
        else {
            DexKit.findFieldWithCache("light_dark_mode") {
                matcher {
                    declaredClass(oneCityBgColorMgrClass!!.name, StringMatchType.Equals)
                    type = "int"
                    addReadMethod(colorModeMethod!!.serialize())
                    addWriteMethod(colorModeMethod!!.serialize())
                }
                searchClasses = oneCityBgColorMgrClass?.let { listOf(it) }
            }
        }
    }

    override fun onHook() {
        if (cardColor != 0 && appClassLoader != null) {
            val mLightDarkMode = mLightDarkModeField?.getFieldInstance(appClassLoader!!) ?: return
            colorModeMethod?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    mLightDarkMode.set(this.instance, mLightDarkModeValue)
                }
            }
        }
    }
}