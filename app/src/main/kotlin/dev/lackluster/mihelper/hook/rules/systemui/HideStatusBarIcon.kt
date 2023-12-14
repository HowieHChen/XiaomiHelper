package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs

object HideStatusBarIcon : YukiBaseHooker() {
    private val hideAirplane = Prefs.getInt(PrefKey.STATUSBAR_HIDE_FLIGHT_MODE, 0)
    private val hideAlarmClock = Prefs.getInt(PrefKey.STATUSBAR_HIDE_ALARM, 0)
    private val hideBluetooth = Prefs.getInt(PrefKey.STATUSBAR_HIDE_BLUETOOTH, 0)
    private val hideBluetoothBattery = Prefs.getInt(PrefKey.STATUSBAR_HIDE_BLUETOOTH_BATTERY, 0)
    private val hideHeadset = Prefs.getInt(PrefKey.STATUSBAR_HIDE_HEADSET, 0)
    private val hideHotspot = Prefs.getInt(PrefKey.STATUSBAR_HIDE_HOTSPOT, 0)
    private val hideLocation = Prefs.getInt(PrefKey.STATUSBAR_HIDE_GPS, 0)
    private val hideNetSpeed = Prefs.getInt(PrefKey.STATUSBAR_HIDE_NET_SPEED, 0)
    private val hideNFC = Prefs.getInt(PrefKey.STATUSBAR_HIDE_NFC, 0)
    private val hideVolume = Prefs.getInt(PrefKey.STATUSBAR_HIDE_VOLUME, 0)
    private val hideVPN = Prefs.getInt(PrefKey.STATUSBAR_HIDE_VPN, 0)
    private val hideZEN = Prefs.getInt(PrefKey.STATUSBAR_HIDE_ZEN, 0)
    private val hideWifi = Prefs.getInt(PrefKey.STATUSBAR_HIDE_WIFI, 0)
    private val hideNoSim = Prefs.getInt(PrefKey.STATUSBAR_HIDE_NO_SIM, 0)
    private val hideNewHD = Prefs.getInt(PrefKey.STATUSBAR_HIDE_HD_NEW, 0)
    private val hideCar = Prefs.getInt(PrefKey.STATUSBAR_HIDE_CAR, 0)
    private val hidePad = Prefs.getInt(PrefKey.STATUSBAR_HIDE_PAD, 0)
    private val hidePC = Prefs.getInt(PrefKey.STATUSBAR_HIDE_PC, 0)
    private val hidePhone = Prefs.getInt(PrefKey.STATUSBAR_HIDE_PHONE, 0)
    private val hideSoundBox = Prefs.getInt(PrefKey.STATUSBAR_HIDE_SOUND_BOX, 0)
    private val hideSoundBoxGroup = Prefs.getInt(PrefKey.STATUSBAR_HIDE_SOUND_BOX_GROUP, 0)
    private val hideSoundBoxScreen = Prefs.getInt(PrefKey.STATUSBAR_HIDE_SOUND_BOX_SCREEN, 0)
    private val hideStereo = Prefs.getInt(PrefKey.STATUSBAR_HIDE_STEREO, 0)
    private val hideTV = Prefs.getInt(PrefKey.STATUSBAR_HIDE_TV, 0)
    private val hideWirelessHeadset = Prefs.getInt(PrefKey.STATUSBAR_HIDE_WIRELESS_HEADSET, 0)

    override fun onHook() {
        val statusBarList = "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClass()
            .field {
                name = "RIGHT_BLOCK_LIST"
                modifiers { isStatic }
            }
            .get().list<String>().toMutableList()
        val controlList = "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClass()
            .field {
                name = "CONTROL_CENTER_BLOCK_LIST"
                modifiers { isStatic }
            }
            .get().list<String>().toMutableList()
        hideIcon(hideAirplane, "airplane", statusBarList, controlList)
        hideIcon(hideAlarmClock, "alarm_clock", statusBarList, controlList)
        hideIcon(hideBluetooth, "bluetooth", statusBarList, controlList)
        hideIcon(hideBluetoothBattery, "bluetooth_handsfree_battery", statusBarList, controlList)
        hideIcon(hideHeadset, "headset", statusBarList, controlList)
        hideIcon(hideHotspot, "hotspot", statusBarList, controlList)
        hideIcon(hideLocation, "location", statusBarList, controlList)
        hideIcon(hideNetSpeed, "network_speed", statusBarList, controlList)
        hideIcon(hideNFC, "nfc", statusBarList, controlList)
        hideIcon(hideVolume, "volume", statusBarList, controlList)
        hideIcon(hideVPN, "vpn", statusBarList, controlList)
        hideIcon(hideZEN, "zen", statusBarList, controlList)
        hideIcon(hideWifi, "wifi", statusBarList, controlList)
        hideIcon(hideNoSim, "no_sim", statusBarList, controlList)
        hideIcon(hideNewHD, "hd", statusBarList, controlList)
        hideIcon(hideCar, "car", statusBarList, controlList)
        hideIcon(hidePad, "pad", statusBarList, controlList)
        hideIcon(hidePC, "pc", statusBarList, controlList)
        hideIcon(hidePhone, "phone", statusBarList, controlList)
        hideIcon(hideSoundBox, "sound_box", statusBarList, controlList)
        hideIcon(hideSoundBoxGroup, "sound_box_group", statusBarList, controlList)
        hideIcon(hideSoundBoxScreen, "sound_box_screen", statusBarList, controlList)
        hideIcon(hideStereo, "stereo", statusBarList, controlList)
        hideIcon(hideTV, "tv", statusBarList, controlList)
        hideIcon(hideWirelessHeadset, "wireless_headset", statusBarList, controlList)
        "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClass()
            .field {
                name = "RIGHT_BLOCK_LIST"
                modifiers { isStatic }
            }
            .get().set(statusBarList)
        "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClass()
            .field {
                name = "CONTROL_CENTER_BLOCK_LIST"
                modifiers { isStatic }
            }
            .get().set(controlList)
    }

    private fun hideIcon(value: Int, name: String, statusBarList: MutableList<String>, controlList: MutableList<String>) {
        when (value) {
            1 -> {
                if (statusBarList.contains(name)) statusBarList.remove(name)
                if (controlList.contains(name)) controlList.remove(name)
            }
            2 -> {
                if (statusBarList.contains(name)) statusBarList.remove(name)
                if (!controlList.contains(name)) controlList.add(name)
            }
            3 -> {
                if (!statusBarList.contains(name)) statusBarList.add(name)
                if (controlList.contains(name)) controlList.remove(name)
            }
            4 -> {
                if (!statusBarList.contains(name)) statusBarList.add(name)
                if (!controlList.contains(name)) controlList.add(name)
            }
            else -> return
        }
    }
}