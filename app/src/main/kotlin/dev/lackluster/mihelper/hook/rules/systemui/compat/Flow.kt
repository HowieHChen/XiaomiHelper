/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2025 HowieHChen, howie.dev@outlook.com

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

package dev.lackluster.mihelper.hook.rules.systemui.compat

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzCoroutineScope
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzEmptyCoroutineContext
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzJavaAdapterKt
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzJob
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMainDispatcherLoader
import java.util.function.Consumer
import kotlin.getValue

object Flow {
    private val metCollectFlow by lazy {
        clzJavaAdapterKt?.resolve()?.firstMethodOrNull {
            name = "collectFlow"
            parameters(
                "kotlinx.coroutines.CoroutineScope",
                "kotlin.coroutines.CoroutineContext",
                "kotlinx.coroutines.flow.StateFlow",
                "java.util.function.Consumer",
            )
            modifiers(Modifiers.STATIC)
        }
    }
    private val metGetCoroutineContext by lazy {
        clzCoroutineScope?.resolve()?.firstMethodOrNull {
            name = "getCoroutineContext"
        }
    }
    private val metJobCancel by lazy {
        clzJob?.resolve()?.firstMethodOrNull {
            name = "cancel"
            parameterCount = 1
        }
    }
    private val EmptyCoroutineContext by lazy {
        clzEmptyCoroutineContext?.resolve()?.firstField {
            name = "INSTANCE"
            modifiers(Modifiers.STATIC)
        }?.get()
    }
    private val MainDispatcher by lazy {
        clzMainDispatcherLoader?.resolve()?.firstField {
            name = "dispatcher"
            modifiers(Modifiers.STATIC)
        }?.get()
    }

    fun cancelJob(job: Any?) {
        job?.let {
            metJobCancel?.copy()?.of(it)?.invoke(null)
        }
    }

    fun <A, B, R> combineFlows(
        scope: Any?,
        src1: IStateFlowCompat<A>,
        defValue1: A? = null,
        src2: IStateFlowCompat<B>,
        defValue2: B? = null,
        dst: MutableStateFlowCompat<R>,
        bifunction: (a: A, b: B) -> R
    ): Pair<Any?, Any?> {
        var value1: A? = defValue1
        var value2: B? = defValue2
        val job1 = src1.collectFlow(scope) { it1 ->
            if (it1 == value1) return@collectFlow
            value1 = it1
            value2?.let { it2 ->
                dst.setValue(bifunction.invoke(it1, it2))
            }
        }
        val job2 = src2.collectFlow(scope) { it2 ->
            if (it2 == value2) return@collectFlow
            value2 = it2
            value1?.let { it1 ->
                dst.setValue(bifunction.invoke(it1, it2))
            }
        }
        return Pair(job1, job2)
    }

    fun <T> IStateFlowCompat<T>.collectFlow(scope: Any?, consumer: Consumer<T>): Any? {
        return collectFlow(scope, EmptyCoroutineContext, this, consumer)
    }

    fun <T> collectFlow(scope: Any?, collectContext: Any?, stateFlow: IStateFlowCompat<T>, consumer: Consumer<T>): Any? {
        return if (scope == null || collectContext == null || stateFlow.real == null) {
            null
        } else {
            metCollectFlow?.copy()?.invoke(scope, collectContext, stateFlow.real, consumer)
        }
    }
}