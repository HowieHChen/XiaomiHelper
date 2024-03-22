/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project

 * This file references HyperCeiler <https://github.com/ReChronoRain/HyperCeiler/blob/main/app/src/main/java/com/sevtinge/hyperceiler/utils/BackupUtils.java>
 * Copyright (C) 2023-2024 HyperCeiler Contributions
 * Convert the code to Kotlin, modified by HowieHChen (howie.dev@outlook.com) on 03/20/2024

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import cn.fkj233.ui.activity.MIUIActivity
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object BackupUtils {
    const val WRITE_DOCUMENT_CODE = 699050
    const val READ_DOCUMENT_CODE = 768955
    private const val BACKUP_FILE_PREFIX = "hyper_helper_backup_"

    fun backup(activity: Activity) {
        val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
        val backupFileName = BACKUP_FILE_PREFIX + timeFormatter.format(LocalDateTime.now())
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/json")
        intent.putExtra(Intent.EXTRA_TITLE, backupFileName)
        activity.startActivityForResult(intent, WRITE_DOCUMENT_CODE)
    }

    fun restore(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/json")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        activity.startActivityForResult(intent, READ_DOCUMENT_CODE)
    }

    fun handleBackup(activity: MIUIActivity, data: Uri?) {
        val sp = MIUIActivity.safeSP.mSP
        if (data == null || sp == null) return
        val outputStream = activity.contentResolver.openOutputStream(data) ?: throw IOException("Can't open output stream")
        val jsonObject = JSONObject()
        for (entry in sp.all) {
            jsonObject.put(entry.key, entry.value)
        }
        outputStream.bufferedWriter().use { it.write(jsonObject.toString()) }
    }

    fun handleRestore(activity: MIUIActivity, data: Uri?) {
        val sp = MIUIActivity.safeSP.mSP
        if (data == null || sp == null) return
        val inputStream = activity.contentResolver.openInputStream(data) ?: throw IOException("Can't open input stream")
        val allText = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(allText)
        val editor = sp.edit()
        for (key in jsonObject.keys()) {
            val value = jsonObject[key]
            if (value is Boolean) {
                editor.putBoolean(key, value)
            } else if (value is Int) {
                editor.putInt(key, value)
            } else if (value is Float) {
                editor.putFloat(key, value)
            } else if (value is String) {
                if (value.contains("[") && value.contains("]")) {
                    val stringList = value.replace("[", "").replace("]", "").replace(" ", "").split(",")
                    val stringSet = HashSet<String>(stringList)
                    editor.putStringSet(key, stringSet)
                } else {
                    editor.putString(key, value)
                }
            }
        }
        editor.apply()
    }
}