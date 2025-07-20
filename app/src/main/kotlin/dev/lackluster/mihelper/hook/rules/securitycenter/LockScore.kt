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

import android.os.Message
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object LockScore : YukiBaseHooker() {
    private val scanJobServiceClass by lazy {
        "com.miui.securityscan.job.ScanJobService".toClassOrNull()
    }
    private val queueIdleClass by lazy {
        DexKit.findClassWithCache("lock_score_queue") {
            matcher {
                className("com.miui.securityscan.MainFragment", StringMatchType.StartsWith)
                addInterface("android.os.MessageQueue\$IdleHandler", StringMatchType.Equals)
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
        DexKit.dexKitBridge.getClassData("com.miui.securityscan.MainFragment")
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
            searchClasses = mainFragment?.let { listOf(it) }
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
            searchClasses = mainFragment?.let { listOf(it) }
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
        DexKit.dexKitBridge.getClassData("com.miui.securityscan.scanner.ScoreManager")
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
            searchClasses = scoreManagerClass?.let { listOf(it) }
        }
    }
    private val getMinusScoreMethod by lazy {
        DexKit.findMethodWithCache("lock_score_minus") {
            matcher {
                returnType = "int"
                addUsingString("getMinusPredictScore", StringMatchType.StartsWith)
            }
            searchClasses = scoreManagerClass?.let { listOf(it) }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.LOCK_SCORE) {
            if (appClassLoader == null) return@hasEnable
            scanJobServiceClass?.apply {
                method {
                    name = "onStartJob"
                }.hook {
                    replaceToFalse()
                }
            }
            queueIdleClass?.getInstance(appClassLoader!!)?.apply {
                method {
                    name = "queueIdle"
                }.hook {
                    intercept()
                }
            }
            scanRunnableMethod?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            redundantScanMethod1?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            redundantScanMethod2?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    val message = this.args(0).cast<Message>() ?: return@before
                    if (message.what == 801) {
                        this.result = null
                    }
                }
            }
            getCacheMinusScoreMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceTo(0)
            }
            getMinusScoreMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceTo(0)
            }
            onRestartSetTextMethod.filter {
                it != redundantScanMethod1
            }.forEach {
                it.getMethodInstance(appClassLoader!!).hook {
                    intercept()
                }
            }
        }
    }
}