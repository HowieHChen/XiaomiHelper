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

import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Pref
import org.json.JSONObject
import java.io.IOException
import kotlin.system.exitProcess


object BackupUtils {
    const val WRITE_DOCUMENT_CODE = 699050
    const val READ_DOCUMENT_CODE = 768955
    const val BACKUP_FILE_PREFIX = "hyper_helper_backup_"

    fun restartApp(context: Context) {
        val intent =
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        exitProcess(0)
    }

    fun handleBackup(context: Context, data: Uri?) {
        val sp = SafeSP.mSP
        if (data == null || sp == null) return
        val outputStream = context.contentResolver.openOutputStream(data) ?: throw IOException("Can't open output stream")
        val jsonObject = JSONObject()
        for (entry in sp.all) {
            when (entry.value) {
                is Int -> {
                    jsonObject.put(entry.key, "#i#${entry.value}")
                }
                is Float -> {
                    jsonObject.put(entry.key, "#f#${entry.value}")
                }
                else -> {
                    jsonObject.put(entry.key, entry.value)
                }
            }
        }
        outputStream.bufferedWriter().use { it.write(jsonObject.toString()) }
    }

    fun handleRestore(context: Context, data: Uri?) {
        val sp = SafeSP.mSP
        if (data == null || sp == null) return
        val inputStream = context.contentResolver.openInputStream(data) ?: throw IOException("Can't open input stream")
        val allText = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(allText)
        if (!checkBackupFileValid(jsonObject)) {
            throw IllegalArgumentException(context.getString(R.string.module_restore_failure_spversion))
        }
        val editor = sp.edit()
        for (key in jsonObject.keys()) {
            when (val value = jsonObject[key]) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> {
                    if (value.contains("[") && value.contains("]")) {
                        val stringList = value.replace("[", "").replace("]", "").replace(" ", "").split(",")
                        val stringSet = HashSet<String>(stringList)
                        editor.putStringSet(key, stringSet)
                    } else if (value.startsWith("#i#")) {
                        editor.putInt(key, value.replace("#i#", "").toInt())
                    } else if (value.startsWith("#f")) {
                        editor.putFloat(key, value.replace("#f#", "").toFloat())
                    } else {
                        editor.putString(key, value)
                    }
                }
            }
        }
        editor.apply()
    }

    fun handleReset(): Boolean {
        val editor = SafeSP.mSP?.edit() ?: return false
        editor.clear()
        editor.apply()
        return true
    }

    private fun checkBackupFileValid(jsonObject: JSONObject): Boolean {
        if (!jsonObject.has(Pref.Key.Module.SP_VERSION)) {
            return true
        }
        return when (val value = jsonObject[Pref.Key.Module.SP_VERSION]) {
            is Int -> true
            is String -> {
                val version = value.replace("#i#", "").toInt()
                version <= Pref.VERSION
            }
            else -> false
        }
    }
}