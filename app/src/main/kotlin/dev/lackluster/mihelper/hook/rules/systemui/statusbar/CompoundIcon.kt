package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import android.content.Context
import android.os.Handler
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.data.Constants.COMPOUND_ICON_PRIORITY_STR
import dev.lackluster.mihelper.data.Constants.IconSlots
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_alarm
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_gps_on
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_quiet_mode
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_ringer_silent
import dev.lackluster.mihelper.hook.rules.systemui.ResourcesUtils.stat_sys_ringer_vibrate
import dev.lackluster.mihelper.hook.rules.systemui.compat.IconControllerCompat.setIcon
import dev.lackluster.mihelper.hook.rules.systemui.compat.IconControllerCompat.setIconVisibility
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.RemotePreferences.lazyGet
import dev.lackluster.mihelper.hook.utils.extraOf
import dev.lackluster.mihelper.hook.utils.toTyped

object CompoundIcon : StaticHooker() {
    private var Any.mergedIconState by extraOf<CompoundIconVM>("KEY_MERGED_ICON_STATE")

    private val addCompoundIcon by lazy {
        Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON.get() in 1..3
    }
    private val mergeAlarm by Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_ALARM.lazyGet()
    private val mergeDnd by Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_ZEN.lazyGet()
    private val mergeLocation by Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_LOCATION.lazyGet()
    private val mergeRinger by Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_ICON_VOLUME.lazyGet()
    private val iconPriority by Preferences.SystemUI.StatusBar.IconTuner.COMPOUND_PRIORITY.lazyGet()

    private val clzPhoneStatusBarPolicy by "com.android.systemui.statusbar.phone.PhoneStatusBarPolicy".lazyClassOrNull()
    private val clzMiuiPrivacyControllerImpl by "com.android.systemui.statusbar.privacy.MiuiPrivacyControllerImpl".lazyClassOrNull()
    private val metIsCTARequiredLocation by lazy {
        clzMiuiPrivacyControllerImpl?.resolve()?.firstMethodOrNull {
            name = "isCTARequiredLocation"
            modifiers(Modifiers.STATIC)
        }?.toTyped<Boolean>()
    }
    private val metUpdateVolumeZen by lazy {
        clzPhoneStatusBarPolicy?.resolve()?.firstMethodOrNull {
            name = "updateVolumeZen"
        }?.toTyped<Unit>()
    }
    private val fldIconController by lazy {
        clzPhoneStatusBarPolicy?.resolve()?.firstFieldOrNull {
            name = "mIconController"
            superclass()
        }?.toTyped<Any>()
    }

    override fun onInit() {
        updateSelfState(addCompoundIcon)
    }

    override fun onHook() {
        if (mergeDnd) {
            $$$"com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$$ExternalSyntheticLambda3".toClassOrNull()?.apply {
                val classId = resolve().firstFieldOrNull {
                    name {
                        it.endsWith("classId")
                    }
                }?.toTyped<Int>()
                val outer = resolve().firstFieldOrNull {
                    name = "f$0"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "accept"
                }?.hook {
                    val ori = proceed()
                    if (classId?.get(thisObject) == 0) {
                        outer?.get(thisObject)?.let { policy ->
                            metUpdateVolumeZen?.invoke(policy)
                        }
                    }
                    result(ori)
                }
            }
        }
        "com.android.systemui.statusbar.phone.MiuiPhoneStatusBarPolicy".toClassOrNull()?.apply {
            if (mergeDnd || mergeRinger) {
                val fldMuteVisible = resolve().firstFieldOrNull {
                    name = "mMuteVisible"
                    superclass()
                }?.toTyped<Boolean>()
                val fldZenVisible = resolve().firstFieldOrNull {
                    name = "mZenVisible"
                    superclass()
                }?.toTyped<Boolean>()
                val fldMuteIconResId = resolve().firstFieldOrNull {
                    name = "mMuteIconResId"
                    superclass()
                }?.toTyped<Int>()
                resolve().firstMethodOrNull {
                    name = "updateVolumeZen"
                }?.hook {
                    val ori = proceed()
                    val mIconController = fldIconController?.get(thisObject)
                    val mMuteIconResId = fldMuteIconResId?.get(thisObject)
                    val mute = fldMuteVisible?.get(thisObject) ?: false
                    val zen = fldZenVisible?.get(thisObject) ?: false
                    val vibrate = (mMuteIconResId == stat_sys_ringer_vibrate)
                    if (mIconController != null) {
                        getOrPutMergedStatusBarIcon(mIconController).let {
                            it.setDnd(zen)
                            it.setVolume(mute, vibrate)
                            it.updateStateIfNeeded(mIconController)
                        }
                    }
                    result(ori)
                }
            }
            if (mergeLocation) {
                val fldLocationController = resolve().firstFieldOrNull {
                    name = "mLocationController"
                    superclass()
                }?.toTyped<Any>()
                val fldAreActiveLocationRequests = "com.android.systemui.statusbar.policy.LocationControllerImpl".toClassOrNull()
                    ?.resolve()?.firstFieldOrNull {
                        name = "mAreActiveLocationRequests"
                    }?.toTyped<Boolean>()
                resolve().firstMethodOrNull {
                    name = "onLocationActiveChanged"
                }?.hook {
                    val ori = proceed()
                    if (metIsCTARequiredLocation?.invoke(null) != true) {
                        val mIconController = fldIconController?.get(thisObject)
                        val locationController = fldLocationController?.get(thisObject)
                        val location = fldAreActiveLocationRequests?.get(locationController) == true
                        if (mIconController != null) {
                            getOrPutMergedStatusBarIcon(mIconController).let {
                                it.setLocating(location)
                                it.updateStateIfNeeded(mIconController)
                            }
                        }
                    }
                    result(ori)
                }
            }
        }
        if (mergeLocation) {
            clzMiuiPrivacyControllerImpl?.apply {
                val fldContext = resolve().firstFieldOrNull {
                    name = "mContext"
                }?.toTyped<Context>()
                val metGetMainThreadHandler = Context::class.resolve().firstMethodOrNull {
                    name = "getMainThreadHandler"
                    superclass()
                }?.toTyped<Handler>()
                val fldIconController = resolve().firstFieldOrNull {
                    name = "mStatusBarIconController"
                }?.toTyped<Any>()
                val metGet = "dagger.Lazy".toClassOrNull()?.resolve()?.firstMethodOrNull {
                    name = "get"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name {
                        it.startsWith("onLocationActiveChanged")
                    }
                }?.hook {
                    val ori = proceed()
                    if (metIsCTARequiredLocation?.invoke(null) == true) {
                        val location = getArg(0) as? Boolean ?: false
                        val mIconControllerLazy = fldIconController?.get(thisObject)
                        val mIconController = mIconControllerLazy?.let { it1 -> metGet?.invoke(it1) }
                        val mContext = fldContext?.get(thisObject)
                        val mainThreadHandler = mContext?.let { it1 -> metGetMainThreadHandler?.invoke(it1) }
                        if (mIconController != null) {
                            mainThreadHandler?.post {
                                getOrPutMergedStatusBarIcon(mIconController).let {
                                    it.setLocating(location)
                                    it.updateStateIfNeeded(mIconController)
                                }
                            }
                        }
                    }
                    result(ori)
                }
            }
        }
        if (mergeAlarm) {
            "com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$4".toClassOrNull()?.apply {
                val outer = resolve().firstFieldOrNull {
                    name = "this$0"
                }?.toTyped<Any>()
                resolve().firstMethodOrNull {
                    name = "onAlarmChanged"
                }?.hook {
                    val ori = proceed()
                    val alarm = getArg(0) as? Boolean ?: false
                    outer?.get(thisObject)?.let { policy ->
                        val mIconController = fldIconController?.get(policy)
                        if (mIconController != null) {
                            getOrPutMergedStatusBarIcon(mIconController).let {
                                it.setNextAlarm(alarm)
                                it.updateStateIfNeeded(mIconController)
                            }
                        }
                    }
                    result(ori)
                }
            }
        }
    }

    private fun getOrPutMergedStatusBarIcon(obj: Any): CompoundIconVM {
        obj.mergedIconState?.let {
            return it
        }
        val state = CompoundIconVM(
            mergeAlarmIcon = mergeAlarm,
            mergeDndIcon = mergeDnd,
            mergeLocationIcon = mergeLocation,
            mergeRingerIcon = mergeRinger,
            priorityString = iconPriority
        )
        obj.mergedIconState = state
        return state
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
                setIcon(iconController, null, IconSlots.COMPOUND_ICON_REAL_LOCATION, stat_sys_gps_on)
                setIcon(iconController, null, IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK, stat_sys_alarm)
                setIcon(iconController, null, IconSlots.COMPOUND_ICON_REAL_ZEN, stat_sys_quiet_mode)
                setIcon(iconController, null, IconSlots.COMPOUND_ICON_REAL_MUTE, stat_sys_ringer_silent)
                setIcon(iconController, null, IconSlots.COMPOUND_ICON_REAL_VIBRATE, stat_sys_ringer_vibrate)
                setIconVisibility(iconController, IconSlots.COMPOUND_ICON_REAL_LOCATION, false)
                setIconVisibility(iconController, IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK, false)
                setIconVisibility(iconController, IconSlots.COMPOUND_ICON_REAL_ZEN, false)
                setIconVisibility(iconController, IconSlots.COMPOUND_ICON_REAL_MUTE, false)
                setIconVisibility(iconController, IconSlots.COMPOUND_ICON_REAL_VIBRATE, false)
            }
            val newVisibleSlot = getIconSlot()
            if (visibleSlot != newVisibleSlot) {
                visibleSlot?.let {
                    setIconVisibility(iconController, it, false)
                }
                newVisibleSlot?.let {
                    setIconVisibility(iconController, it, true)
                }
                visibleSlot = newVisibleSlot
            }
        }
    }
}