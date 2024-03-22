/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.systemui.statusbar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTurner
import dev.lackluster.mihelper.utils.Prefs

object HideStatusBarIcon : YukiBaseHooker() {
    private val hideAirplane = Prefs.getInt(IconTurner.FLIGHT_MODE, 0)
    private val hideAlarmClock = Prefs.getInt(IconTurner.ALARM, 0)
    private val hideBluetooth = Prefs.getInt(IconTurner.BLUETOOTH, 0)
    private val hideBluetoothBattery = Prefs.getInt(IconTurner.BLUETOOTH_BATTERY, 0)
    private val hideHeadset = Prefs.getInt(IconTurner.HEADSET, 0)
    private val hideHotspot = Prefs.getInt(IconTurner.HOTSPOT, 0)
    private val hideLocation = Prefs.getInt(IconTurner.GPS, 0)
    private val hideNetSpeed = Prefs.getInt(IconTurner.NET_SPEED, 0)
    private val hideNFC = Prefs.getInt(IconTurner.NFC, 0)
    private val hideVolume = Prefs.getInt(IconTurner.VOLUME, 0)
    private val hideVPN = Prefs.getInt(IconTurner.VPN, 0)
    private val hideZEN = Prefs.getInt(IconTurner.ZEN, 0)
    private val hideWifi = Prefs.getInt(IconTurner.WIFI, 0)
    private val hideNoSim = Prefs.getInt(IconTurner.NO_SIM, 0)
    private val hideNewHD = Prefs.getInt(IconTurner.HD_NEW, 0)
    private val hideCar = Prefs.getInt(IconTurner.CAR, 0)
    private val hidePad = Prefs.getInt(IconTurner.PAD, 0)
    private val hidePC = Prefs.getInt(IconTurner.PC, 0)
    private val hidePhone = Prefs.getInt(IconTurner.PHONE, 0)
    private val hideSoundBox = Prefs.getInt(IconTurner.SOUND_BOX, 0)
    private val hideSoundBoxGroup = Prefs.getInt(IconTurner.SOUND_BOX_GROUP, 0)
    private val hideSoundBoxScreen = Prefs.getInt(IconTurner.SOUND_BOX_SCREEN, 0)
    private val hideStereo = Prefs.getInt(IconTurner.STEREO, 0)
    private val hideTV = Prefs.getInt(IconTurner.TV, 0)
    private val hideWirelessHeadset = Prefs.getInt(IconTurner.WIRELESS_HEADSET, 0)

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
        "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClass().field {
            name = "RIGHT_BLOCK_LIST"
            modifiers { isStatic }
        }.get().set(statusBarList)
        "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClass().field {
            name = "CONTROL_CENTER_BLOCK_LIST"
            modifiers { isStatic }
        }.get().set(controlList)
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