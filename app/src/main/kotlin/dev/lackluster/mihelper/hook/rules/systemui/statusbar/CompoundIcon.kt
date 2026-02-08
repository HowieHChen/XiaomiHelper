package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.Context
import android.os.Handler
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants.COMPOUND_ICON_PRIORITY_STR
import dev.lackluster.mihelper.data.Constants.IconSlots
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_alarm
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_gps_on
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_quiet_mode
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_ringer_silent
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_ringer_vibrate
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.getAdditionalInstanceField
import dev.lackluster.mihelper.utils.factory.setAdditionalInstanceField

object CompoundIcon : YukiBaseHooker() {
    private const val KEY_MERGED_ICON_STATE = "KEY_MERGED_ICON_STATE"
    private val addCompoundIcon = Prefs.getInt(IconTuner.COMPOUND_ICON, 0) in 1..3
    private val mergeAlarm = Prefs.getBoolean(IconTuner.COMPOUND_ICON_ALARM, false)
    private val mergeDnd = Prefs.getBoolean(IconTuner.COMPOUND_ICON_ZEN, false)
    private val mergeLocation = Prefs.getBoolean(IconTuner.COMPOUND_ICON_LOCATION, false)
    private val mergeRinger = Prefs.getBoolean(IconTuner.COMPOUND_ICON_VOLUME, false)
    private val iconPriority = Prefs.getString(IconTuner.COMPOUND_PRIORITY, COMPOUND_ICON_PRIORITY_STR) ?: COMPOUND_ICON_PRIORITY_STR

    private val clzPhoneStatusBarPolicy by lazy {
        "com.android.systemui.statusbar.phone.PhoneStatusBarPolicy".toClassOrNull()
    }
    private val clzStatusBarIconControllerImpl by lazy {
        "com.android.systemui.statusbar.phone.ui.StatusBarIconControllerImpl".toClassOrNull()
    }
    private val clzMiuiPrivacyControllerImpl by lazy {
        "com.android.systemui.statusbar.privacy.MiuiPrivacyControllerImpl".toClassOrNull()
    }
    private val metIsCTARequiredLocation by lazy {
        clzMiuiPrivacyControllerImpl?.resolve()?.firstMethodOrNull {
            name = "isCTARequiredLocation"
            modifiers(Modifiers.STATIC)
        }
    }
    private val metSetIcon by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstMethodOrNull {
            name = "setIcon"
            parameters(CharSequence::class, String::class, Int::class)
        }?.self?.apply { makeAccessible() }
    }
    private val metSetIconVisibility by lazy {
        clzStatusBarIconControllerImpl?.resolve()?.firstMethodOrNull {
            name = "setIconVisibility"
            parameters(String::class, Boolean::class)
        }?.self?.apply { makeAccessible() }
    }
    private val metUpdateVolumeZen by lazy {
        clzPhoneStatusBarPolicy?.resolve()?.firstMethodOrNull {
            name = "updateVolumeZen"
        }?.self?.apply { makeAccessible() }
    }
    private val fldIconController by lazy {
        clzPhoneStatusBarPolicy?.resolve()?.firstFieldOrNull {
            name = "mIconController"
            superclass()
        }?.self?.apply { makeAccessible() }
    }

    override fun onHook() {
        if (addCompoundIcon) {
            if (mergeDnd) {
                $$$"com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$$ExternalSyntheticLambda3".toClassOrNull()?.apply {
                    val classId = resolve().firstFieldOrNull {
                        name {
                            it.endsWith("classId")
                        }
                    }?.self?.apply { makeAccessible() }
                    val outer = resolve().firstFieldOrNull {
                        name = "f$0"
                    }?.self?.apply { makeAccessible() }
                    resolve().firstMethodOrNull {
                        name = "accept"
                    }?.hook {
                        after {
                            if (classId?.getInt(this.instance) == 0) {
                                outer?.get(this.instance)?.let { policy ->
                                    metUpdateVolumeZen?.invoke(policy)
                                }
                            }
                        }
                    }
                }
            }
            "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarPolicy".toClassOrNull()?.apply {
                if (mergeDnd || mergeRinger) {
                    val fldMuteVisible = resolve().firstFieldOrNull {
                        name = "mMuteVisible"
                        superclass()
                    }?.self?.apply { makeAccessible() }
                    val fldZenVisible = resolve().firstFieldOrNull {
                        name = "mZenVisible"
                        superclass()
                    }?.self?.apply { makeAccessible() }
                    val fldMuteIconResId = resolve().firstFieldOrNull {
                        name = "mMuteIconResId"
                        superclass()
                    }?.self?.apply { makeAccessible() }
                    resolve().firstMethodOrNull {
                        name = "updateVolumeZen"
                    }?.hook {
                        after {
                            val mIconController = fldIconController?.get(this.instance) ?: return@after
                            val mMuteIconResId = fldMuteIconResId?.getInt(this.instance)
                            val mute = (fldMuteVisible?.getBoolean(this.instance) == true)
                            val zen = (fldZenVisible?.getBoolean(this.instance) == true)
                            val vibrate = (mMuteIconResId == stat_sys_ringer_vibrate)
                            getOrPutMergedStatusBarIcon(mIconController).let {
                                it.setDnd(zen)
                                it.setVolume(mute, vibrate)
                                it.updateStateIfNeeded(mIconController)
                            }
                        }
                    }
                }
                if (mergeLocation) {
                    val fldLocationController = resolve().firstFieldOrNull {
                        name = "mLocationController"
                        superclass()
                    }?.self?.apply { makeAccessible() }
                    val fldAreActiveLocationRequests = "com.android.systemui.statusbar.policy.LocationControllerImpl".toClassOrNull()
                        ?.resolve()?.firstFieldOrNull {
                            name = "mAreActiveLocationRequests"
                        }?.self?.apply { makeAccessible() }
                    resolve().firstMethodOrNull {
                        name = "onLocationActiveChanged"
                    }?.hook {
                        after {
                            if (metIsCTARequiredLocation?.invoke<Boolean>() != true) {
                                val mIconController = fldIconController?.get(this.instance) ?: return@after
                                val locationController = fldLocationController?.get(this.instance)
                                val location = fldAreActiveLocationRequests?.getBoolean(locationController) == true
                                getOrPutMergedStatusBarIcon(mIconController).let {
                                    it.setLocating(location)
                                    it.updateStateIfNeeded(mIconController)
                                }
                            }
                        }
                    }
                }
            }
            if (mergeLocation) {
                clzMiuiPrivacyControllerImpl?.apply {
                    val fldContext = resolve().firstFieldOrNull {
                        name = "mContext"
                    }?.self?.apply { makeAccessible() }
                    val metGetMainThreadHandler = Context::class.resolve().firstMethodOrNull {
                        name = "getMainThreadHandler"
                        superclass()
                    }?.self?.apply { makeAccessible() }
                    val fldIconController = resolve().firstFieldOrNull {
                        name = "mStatusBarIconController"
                    }?.self?.apply { makeAccessible() }
                    val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                        name = "get"
                    }?.self
                    resolve().firstMethodOrNull {
                        name {
                            it.startsWith("onLocationActiveChanged")
                        }
                    }?.hook {
                        after {
                            if (metIsCTARequiredLocation?.invoke<Boolean>() == true) {
                                val location = this.args(0).boolean()
                                val mIconControllerLazy = fldIconController?.get(this.instance)
                                val mIconController = mIconControllerLazy?.let { it1 -> metGet?.invoke(it1) } ?: return@after
                                val mContext = fldContext?.get(this.instance) as? Context
                                val mainThreadHandler = mContext?.let { it1 -> metGetMainThreadHandler?.invoke(it1) as? Handler } ?: return@after
                                mainThreadHandler.post {
                                    getOrPutMergedStatusBarIcon(mIconController).let {
                                        it.setLocating(location)
                                        it.updateStateIfNeeded(mIconController)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (mergeAlarm) {
                "com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$4".toClassOrNull()?.apply {
                    val outer = resolve().firstFieldOrNull {
                        name = "this$0"
                    }?.self?.apply { makeAccessible() }
                    resolve().firstMethodOrNull {
                        name = "onAlarmChanged"
                    }?.hook {
                        after {
                            val alarm = this.args(0).boolean()
                            outer?.get(this.instance)?.let { policy ->
                                val mIconController = fldIconController?.get(policy) ?: return@after
                                getOrPutMergedStatusBarIcon(mIconController).let {
                                    it.setNextAlarm(alarm)
                                    it.updateStateIfNeeded(mIconController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getOrPutMergedStatusBarIcon(obj: Any): CompoundIconVM {
        return obj.getAdditionalInstanceField<CompoundIconVM>(KEY_MERGED_ICON_STATE)
            ?: CompoundIconVM(
                mergeAlarmIcon = mergeAlarm,
                mergeDndIcon = mergeDnd,
                mergeLocationIcon = mergeLocation,
                mergeRingerIcon = mergeRinger,
                priorityString = iconPriority
            ).also {
                obj.setAdditionalInstanceField(KEY_MERGED_ICON_STATE, it)
            }
    }

    class CompoundIconVM(
        mergeAlarmIcon: Boolean = false,
        mergeDndIcon: Boolean = false,
        mergeLocationIcon: Boolean = false,
        mergeRingerIcon: Boolean = false,
        priorityString: String = COMPOUND_ICON_PRIORITY_STR
    ) {
        private val priority: List<String>
        private var visibleSlot: String? = null

        private val states = mutableMapOf(
            IconSlots.COMPOUND_ICON_REAL_LOCATION to false,
            IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK to false,
            IconSlots.COMPOUND_ICON_REAL_ZEN to false,
            IconSlots.COMPOUND_ICON_REAL_MUTE to false,
            IconSlots.COMPOUND_ICON_REAL_VIBRATE to false,
        )

        init {
            val mutableList = mutableListOf<String>()
            priorityString.split(',', ' ', '，').forEach {
                when (it) {
                    IconSlots.LOCATION -> if (mergeLocationIcon) mutableList.add(IconSlots.COMPOUND_ICON_REAL_LOCATION)
                    IconSlots.ALARM_CLOCK -> if (mergeAlarmIcon) mutableList.add(IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK)
                    IconSlots.ZEN -> if (mergeDndIcon) mutableList.add(IconSlots.COMPOUND_ICON_REAL_ZEN)
                    IconSlots.VOLUME -> if (mergeRingerIcon) {
                        mutableList.add(IconSlots.COMPOUND_ICON_REAL_VIBRATE)
                        mutableList.add(IconSlots.COMPOUND_ICON_REAL_MUTE)
                    }
                }
            }
            priority = mutableList.toList()
        }

        fun setLocating(enabled: Boolean) {
            states[IconSlots.COMPOUND_ICON_REAL_LOCATION] = enabled
        }

        fun setNextAlarm(hasNextAlarm: Boolean) {
            states[IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK] = hasNextAlarm
        }

        fun setDnd(enabled: Boolean) {
            states[IconSlots.COMPOUND_ICON_REAL_ZEN] = enabled
        }

        fun setVolume(mute: Boolean, vibrate: Boolean) {
            states[IconSlots.COMPOUND_ICON_REAL_MUTE] = (mute && !vibrate)
            states[IconSlots.COMPOUND_ICON_REAL_VIBRATE] = (mute && vibrate)
        }

        private fun getIconSlot(): String? {
            return priority.firstOrNull {
                states[it] == true
            }
        }

        fun updateStateIfNeeded(iconController: Any) {
            if (visibleSlot == null) {
                metSetIcon?.invoke(iconController, null, IconSlots.COMPOUND_ICON_REAL_LOCATION, stat_sys_gps_on)
                metSetIcon?.invoke(iconController, null, IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK, stat_sys_alarm)
                metSetIcon?.invoke(iconController, null, IconSlots.COMPOUND_ICON_REAL_ZEN, stat_sys_quiet_mode)
                metSetIcon?.invoke(iconController, null, IconSlots.COMPOUND_ICON_REAL_MUTE, stat_sys_ringer_silent)
                metSetIcon?.invoke(iconController, null, IconSlots.COMPOUND_ICON_REAL_VIBRATE, stat_sys_ringer_vibrate)
                metSetIconVisibility?.invoke(iconController, IconSlots.COMPOUND_ICON_REAL_LOCATION, false)
                metSetIconVisibility?.invoke(iconController, IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK, false)
                metSetIconVisibility?.invoke(iconController, IconSlots.COMPOUND_ICON_REAL_ZEN, false)
                metSetIconVisibility?.invoke(iconController, IconSlots.COMPOUND_ICON_REAL_MUTE, false)
                metSetIconVisibility?.invoke(iconController, IconSlots.COMPOUND_ICON_REAL_VIBRATE, false)
            }
            val newVisibleSlot = getIconSlot()
            if (visibleSlot != newVisibleSlot) {
                visibleSlot?.let {
                    metSetIconVisibility?.invoke(iconController, it, false)
                }
                newVisibleSlot?.let {
                    metSetIconVisibility?.invoke(iconController, it, true)
                }
                visibleSlot = newVisibleSlot
            }
        }
    }
}