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
import dev.lackluster.mihelper.data.Pref.Key.SystemUI.IconTuner
import dev.lackluster.mihelper.utils.Prefs

object IconManager : YukiBaseHooker() {
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
            "location" to IconTuner.LOCATION,
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
            "alarm_clock" to IconTuner.ALARM_CLOCK,
            "zen" to IconTuner.ZEN,
            "volume" to IconTuner.VOLUME,
            "second_space" to IconTuner.SECOND_SPACE,
        ).forEach { slot, key ->
            handleIcon(
                Prefs.getInt(key, 0),
                slot,
                listStatusBar,
                listControlCenter
            )
        }
        rightBlockList.copy().set(listStatusBar)
        controlCenterBlockList.copy().set(listControlCenter)
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