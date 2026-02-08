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

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Constants.COMPOUND_ICON_REAL_SLOTS
import dev.lackluster.mihelper.data.Constants.IconSlots
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.utils.Prefs

object IconManager : YukiBaseHooker() {
    private val iconPositionMode = Prefs.getInt(IconTuner.ICON_POSITION, 0)
    private val addCompoundIcon = Prefs.getInt(IconTuner.COMPOUND_ICON, 0) in 1..3
    private val leftContainer = Prefs.getBoolean(IconTuner.LEFT_CONTAINER, false)
    private val leftCompoundIcon = Prefs.getBoolean(IconTuner.LEFT_COMPOUND_ICON, false)
    private val leftLocation = Prefs.getBoolean(IconTuner.LEFT_LOCATION, false)
    private val leftAlarmClock = Prefs.getBoolean(IconTuner.LEFT_ALARM_CLOCK, false)
    private val leftZen = Prefs.getBoolean(IconTuner.LEFT_ZEN, false)
    private val leftVolume = Prefs.getBoolean(IconTuner.LEFT_VOLUME, false)

    private val slotsCustom by lazy {
        Prefs.getStringSet(
            IconTuner.ICON_POSITION_VAL,
            mutableSetOf()
        ).mapNotNull { str ->
            str.split(":").takeIf { it.size == 2 }
        }.sortedBy {
            it[0].toInt()
        }.map { it[1] }.takeIf {
            it.isNotEmpty()
        } ?: Constants.STATUS_BAR_ICONS_DEFAULT
    }
    private val finalSlots by lazy {
        when (iconPositionMode) {
            1 -> Constants.STATUS_BAR_ICONS_SWAP
            2 -> slotsCustom
            else -> Constants.STATUS_BAR_ICONS_DEFAULT
        }.let { array ->
            val slotsList = array.toMutableList()
            if (addCompoundIcon && !slotsList.contains(IconSlots.COMPOUND_ICON_STUB)) {
                slotsList.add(
                    slotsList.indexOf(IconSlots.ZEN),
                    IconSlots.COMPOUND_ICON_STUB
                )
            }
            if (leftContainer) {
                slotsList.sortByDescending { it in leftSlots }
            }
            if (addCompoundIcon) {
                slotsList.addAll(
                    slotsList.indexOf(IconSlots.COMPOUND_ICON_STUB),
                    COMPOUND_ICON_REAL_SLOTS
                )
                slotsList.remove(IconSlots.COMPOUND_ICON_STUB)
            }
            slotsList.toTypedArray()
        }
    }
    private val leftSlots by lazy {
        mutableListOf<String>().apply {
            if (leftCompoundIcon) addAll(COMPOUND_ICON_REAL_SLOTS)
            if (leftLocation) add(IconSlots.LOCATION)
            if (leftAlarmClock) add(IconSlots.ALARM_CLOCK)
            if (leftZen) add(IconSlots.ZEN)
            if (leftVolume) add(IconSlots.VOLUME)
        }.toList()
    }
    val leftBlockList by lazy {
        finalSlots.toMutableList().apply {
            removeAll(leftSlots)
        }.toList()
    }

    override fun onHook() {
        val clzMiuiIconManagerUtils = "com.android.systemui.statusbar.phone.MiuiIconManagerUtils".toClassOrNull()
        val rightBlockList = clzMiuiIconManagerUtils?.resolve()?.firstFieldOrNull {
            name = "RIGHT_BLOCK_LIST"
            modifiers(Modifiers.STATIC)
        }
        val controlCenterBlockList = clzMiuiIconManagerUtils?.resolve()?.firstFieldOrNull {
            name = "CONTROL_CENTER_BLOCK_LIST"
            modifiers(Modifiers.STATIC)
        }
        val listStatusBar = rightBlockList?.copy()?.get<List<String>>()?.toMutableList() ?: return
        val listControlCenter = controlCenterBlockList?.copy()?.get<List<String>>()?.toMutableList() ?: return
        mapOf(
            "mobile" to IconTuner.MOBILE,
            "demo_mobile" to IconTuner.MOBILE,
            "no_sim" to IconTuner.NO_SIM,
            "airplane" to IconTuner.AIRPLANE,
            "wifi" to IconTuner.WIFI,
            "demo_wifi" to IconTuner.WIFI,
            "hotspot" to IconTuner.HOTSPOT,
            "vpn" to IconTuner.VPN,
            "network_speed" to IconTuner.NET_SPEED,
            "bluetooth" to IconTuner.BLUETOOTH,
            "bluetooth_handsfree_battery" to IconTuner.BLUETOOTH_BATTERY,
            "handle_battery" to IconTuner.HANDLE_BATTERY,
            "nfc" to IconTuner.NFC,
            "gps" to IconTuner.LOCATION,
            IconSlots.LOCATION to IconTuner.LOCATION,
            "wireless_headset" to IconTuner.WIRELESS_HEADSET,
            "phone" to IconTuner.PHONE,
            "pad" to IconTuner.PAD,
            "pc" to IconTuner.PC,
            "sound_box_group" to IconTuner.SOUND_BOX_GROUP,
            "stereo" to IconTuner.STEREO,
            "sound_box_screen" to IconTuner.SOUND_BOX_SCREEN,
            "sound_box" to IconTuner.SOUND_BOX,
            "tv" to IconTuner.TV,
            "glasses" to IconTuner.GLASSES,
            "car" to IconTuner.CAR,
            "camera" to IconTuner.CAMERA,
            "dist_compute" to IconTuner.DIST_COMPUTE,
            "headset" to IconTuner.HEADSET,
            IconSlots.ALARM_CLOCK to IconTuner.ALARM_CLOCK,
            IconSlots.ZEN to IconTuner.ZEN,
            IconSlots.VOLUME to IconTuner.VOLUME,
            "second_space" to IconTuner.SECOND_SPACE,
        ).forEach { (slot, key) ->
            handleIcon(
                Prefs.getInt(key, 0),
                slot,
                listStatusBar,
                listControlCenter
            )
        }
        if (addCompoundIcon) {
            mapOf(
                IconSlots.COMPOUND_ICON_REAL_LOCATION to IconTuner.COMPOUND_ICON,
                IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK to IconTuner.COMPOUND_ICON,
                IconSlots.COMPOUND_ICON_REAL_ZEN to IconTuner.COMPOUND_ICON,
                IconSlots.COMPOUND_ICON_REAL_VIBRATE to IconTuner.COMPOUND_ICON,
                IconSlots.COMPOUND_ICON_REAL_MUTE to IconTuner.COMPOUND_ICON,
            ).forEach { (slot, key) ->
                handleIcon(
                    Prefs.getInt(key, 0),
                    slot,
                    listStatusBar,
                    listControlCenter
                )
            }
        }
        if (leftContainer) {
            leftSlots.forEach {
                if (!listStatusBar.contains(it)) {
                    listStatusBar.add(it)
                }
            }
        }
        rightBlockList.copy().set(listStatusBar)
        controlCenterBlockList.copy().set(listControlCenter)
        if (iconPositionMode != 0 || addCompoundIcon) {
            "com.android.systemui.statusbar.phone.ui.StatusBarIconList".toClassOrNull()?.apply {
                resolve().firstConstructorOrNull {
                    parameters(Array<String>::class)
                }?.hook {
                    before {
                        this.args(0).set(finalSlots)
                    }
                }
            }
        }
        if (leftContainer) {
            loadHooker(LeftContainer)
        }
    }

    private fun handleIcon(value: Int, name: String, statusBarList: MutableList<String>, controlList: MutableList<String>) {
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