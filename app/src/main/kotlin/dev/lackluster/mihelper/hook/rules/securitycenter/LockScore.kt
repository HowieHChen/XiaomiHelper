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

package dev.lackluster.mihelper.hook.rules.securitycenter

import android.app.Activity
import android.os.Message
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object LockScore : StaticHooker() {
    private val scanJobServiceClass by "com.miui.securityscan.job.ScanJobService".lazyClassOrNull()
    
    private val queueIdleClass by lazy {
        DexKit.findClassWithCache("lock_score_queue") {
            matcher {
                className("com.miui.securityscan.MainFragment", StringMatchType.StartsWith)
                addInterface($$"android.os.MessageQueue$IdleHandler", StringMatchType.Equals)
            }
        }
    }
    private val scanRunnableMethod by lazy {
        DexKit.findMethodWithCache("lock_score_scan") {
            matcher {
                addUsingString("scMainActivity", StringMatchType.Equals)
                addUsingString("PopOptimizeEntryListener  onFinishScan", StringMatchType.Equals)
            }
        }
    }
    private val mainFragment by lazy {
        DexKit.withBridge { getClassData("com.miui.securityscan.MainFragment") }
    }
    private val onRestartSetTextMethod by lazy {
        DexKit.findMethodsWithCache("lock_score_restart") {
            matcher {
                returnType = "void"
                modifiers = Modifier.PRIVATE
                addInvoke {
                    name = "setActionButtonText"
                }
            }
            searchClasses = listOfNotNull(mainFragment)
        }
    }
    private val redundantScanMethod1 by lazy {
        DexKit.findMethodWithCache("lock_score_redundant1") {
            matcher {
                returnType = "void"
                paramCount = 0
                modifiers(Modifier.PRIVATE)
                addUsingString("incremental_scan_fg", StringMatchType.Equals)
                addUsingString("scan", StringMatchType.Equals)
            }
            searchClasses = listOfNotNull(mainFragment)
        }
    }
    private val redundantScanMethod2 by lazy {
        DexKit.findMethodWithCache("lock_score_redundant2") {
            matcher {
                returnType = "void"
                paramCount = 1
                paramTypes("android.os.Message")
                modifiers(Modifier.PUBLIC)
                addUsingString("VirusScanManager", StringMatchType.Equals)
                addUsingString("update score after incremental scan: ", StringMatchType.Equals)
            }
        }
    }
    private val scoreManagerClass by lazy {
        DexKit.withBridge { getClassData("com.miui.securityscan.scanner.ScoreManager") }
    }
    private val getCacheMinusScoreMethod by lazy {
        DexKit.findMethodWithCache("lock_score_cache_minus") {
            matcher {
                addUsingNumber(0xa4cb800)
                addUsingNumber(0x5265c00)
                addUsingNumber(0x2932e00)
                addUsingNumber(0x1499700)
                addUsingNumber(0xf731400)
            }
            searchClasses = listOfNotNull(scoreManagerClass)
        }
    }
    private val getMinusScoreMethod by lazy {
        DexKit.findMethodWithCache("lock_score_minus") {
            matcher {
                returnType = "int"
                addUsingString("getMinusPredictScore", StringMatchType.StartsWith)
            }
            searchClasses = listOfNotNull(scoreManagerClass)
        }
    }
    private val onExitDialogMethod by lazy {
        DexKit.findMethodWithCache("lock_score_exit_dialog") {
            matcher {
                returnType = "void"
                paramCount = 0
                modifiers = Modifier.PUBLIC
                addUsingNumber(1)
                addUsingNumber(2)
                addUsingNumber(3)
                addUsingNumber(4)
                addUsingNumber(5)
                addInvoke {
                    name = "removeCallbacksAndMessages"
                }
            }
            searchClasses = listOfNotNull(mainFragment)
        }
    }
    private val getScoreInSecurity by lazy {
        DexKit.findMethodWithCache("lock_score_in_memory_get") {
            matcher {
                addUsingString("key_score_in_security", StringMatchType.Equals)
                returnType = "int"
            }
        }
    }
    private val setScoreInSecurity by lazy {
        DexKit.findMethodWithCache("lock_score_in_memory_set") {
            matcher {
                addUsingString("key_score_in_security", StringMatchType.Equals)
                returnType = "void"
            }
        }
    }

    override fun onInit() {
        Preferences.SecurityCenter.LOCK_SCORE.get().also { 
            updateSelfState(it)
        }.ifTrue {
            queueIdleClass
            scanRunnableMethod
            redundantScanMethod1
            redundantScanMethod2
            getCacheMinusScoreMethod
            getMinusScoreMethod
            onRestartSetTextMethod
            onExitDialogMethod
            getScoreInSecurity
            setScoreInSecurity
        }
    }

    override fun onHook() {
        scanJobServiceClass?.apply {
            resolve().firstMethodOrNull {
                name = "onStartJob"
            }?.hook {
                result(false)
            }
        }
        queueIdleClass?.getInstance(classLoader)?.apply {
            resolve().firstMethodOrNull {
                name = "queueIdle"
            }?.hook {
                result(null)
            }
        }
        scanRunnableMethod?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
        redundantScanMethod1?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
        redundantScanMethod2?.getMethodInstance(classLoader)?.hook {
            val message = getArg(0) as? Message
            if (message?.what == 801) {
                result(null)
            } else {
                result(proceed())
            }
        }
        getCacheMinusScoreMethod?.getMethodInstance(classLoader)?.hook {
            result(0)
        }
        getMinusScoreMethod?.getMethodInstance(classLoader)?.hook {
            result(0)
        }
        onRestartSetTextMethod.filter {
            it != redundantScanMethod1
        }.map {
            it.getMethodInstance(classLoader)
        }.hookAll {
            result(null)
        }
        onExitDialogMethod?.getMethodInstance(classLoader)?.apply {
            val getActivity = this.declaringClass.resolve().firstMethodOrNull {
                name = "getActivity"
                superclass()
            }?.toTyped<Activity>()
            hook {
                getActivity?.invoke(thisObject)?.finish()
                result(null)
            }
        }
        getScoreInSecurity?.getMethodInstance(classLoader)?.hook {
            result(100)
        }
        setScoreInSecurity?.getMethodInstance(classLoader)?.hook {
            result(null)
        }
    }
}