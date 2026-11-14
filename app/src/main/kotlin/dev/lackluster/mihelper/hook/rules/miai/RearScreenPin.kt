package dev.lackluster.mihelper.hook.rules.miai

import android.app.Service
import android.content.Intent
import android.net.Uri
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import dev.lackluster.mihelper.data.Constants.ACTION_PIN_TO_REAR_SCREEN
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.hook.utils.toTyped
import dev.lackluster.mihelper.utils.Device
import org.luckypray.dexkit.query.enums.StringMatchType

object RearScreenPin : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    const val EXTRA_TYPE = "type"
    const val EXTRA_TEXT = "text"
    const val EXTRA_IMAGE_URI = "image_uri"

    const val TYPE_TEXT = "text"
    const val TYPE_IMAGE = "image"

    private const val MAX_TEXT_LENGTH = 8_192

    private val dexClzRearSmartAssistantHelper by lazy {
        DexKit.findClassWithCache("rear_smart_assistant_helper") {
            matcher {
                addUsingString("RearSmartAssistantHelper", StringMatchType.Equals)
                addUsingString("com.xiaomi.subscreencenter.service.SubScreenService", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.Taplus.XIAOAI_INTEGRATION.observe {
                updateSelfState(it && Device.isIndependentRearDeviceAndSupportAssistant)
            }
        )
        dexClzRearSmartAssistantHelper
    }

    override fun onStart() {
        val clzRearSmartAssistantHelper = dexClzRearSmartAssistantHelper?.getInstance(classLoader)
        val ctorRearSmartAssistantHelper = clzRearSmartAssistantHelper?.resolve()?.firstConstructorOrNull {
            parameterCount = 0
        }?.toTyped()
        val metPinText = clzRearSmartAssistantHelper?.resolve()?.firstMethodOrNull {
            name = "pinText"
            parameters(String::class)
        }?.toTyped<Unit>()
        val metPinPrepare = clzRearSmartAssistantHelper?.resolve()?.firstMethodOrNull {
            name = "pinPrepare"
        }?.toTyped<Unit>()
        val metPinImage = clzRearSmartAssistantHelper?.resolve()?.firstMethodOrNull {
            name = "pinImage"
            parameters(Uri::class)
        }?.toTyped<Unit>()
        "com.xiaomi.voiceassistant.VoiceService".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "onStartCommand"
            parameters(Intent::class, Int::class, Int::class)
        }?.hook {
            val intent = getArg(0) as? Intent
            if (intent?.action != ACTION_PIN_TO_REAR_SCREEN) {
                return@hook result(proceed())
            }

            when (intent.getStringExtra(EXTRA_TYPE)) {
                TYPE_TEXT -> {
                    intent.getStringExtra(EXTRA_TEXT)
                        ?.takeIf { it.isNotBlank() && it.length <= MAX_TEXT_LENGTH }
                        ?.let { text ->
                            ctorRearSmartAssistantHelper?.newInstance()?.let { helper ->
                                metPinText?.invoke(helper, text)
                            }
                        }
                }
                TYPE_IMAGE -> {
                    intent.getParcelableExtra(EXTRA_IMAGE_URI, classOf<Uri>())?.let { uri ->
                        ctorRearSmartAssistantHelper?.newInstance()?.let { helper ->
                            metPinPrepare?.invoke(helper)
                            metPinImage?.invoke(helper, uri)
                        }
                    }
                }
            }
            result(Service.START_NOT_STICKY)
        }
    }
}
