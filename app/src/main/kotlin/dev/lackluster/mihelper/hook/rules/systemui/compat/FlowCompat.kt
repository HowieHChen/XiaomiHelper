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
import com.highcapable.kavaref.condition.type.VagueType
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzCoroutineScope
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzEmptyCoroutineContext
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzJavaAdapterKt
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzJob
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMainDispatcherLoader
import java.util.function.Consumer
import kotlin.getValue

object FlowCompat {
    private val metCollectFlow by lazy {
        clzJavaAdapterKt?.resolve()?.optional(true)?.firstMethodOrNull {
            name = "collectFlow"
            parameters(
                "kotlinx.coroutines.CoroutineScope",
                "kotlin.coroutines.CoroutineContext",
                "kotlinx.coroutines.flow.StateFlow",
                "java.util.function.Consumer",
            )
            modifiers(Modifiers.STATIC)
        } ?: clzJavaAdapterKt?.resolve()?.optional(true)?.firstMethodOrNull {
            name = "collectFlow"
            parameters(
                "kotlinx.coroutines.CoroutineScope",
                "kotlin.coroutines.CoroutineContext",
                "kotlinx.coroutines.flow.Flow",
                "java.util.function.Consumer",
            )
            modifiers(Modifiers.STATIC)
        } ?: clzJavaAdapterKt?.resolve()?.firstMethodOrNull {
            name = "collectFlow"
            parameters(
                "kotlinx.coroutines.CoroutineScope",
                "kotlin.coroutines.CoroutineContext",
                VagueType,
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
    ): List<Any?> {
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
        return listOf(job1, job2)
    }

    fun <A, B, C, R> combineFlows(
        scope: Any?,
        src1: IStateFlowCompat<A>,
        defValue1: A? = null,
        src2: IStateFlowCompat<B>,
        defValue2: B? = null,
        src3: IStateFlowCompat<C>,
        defValue3: C? = null,
        dst: MutableStateFlowCompat<R>,
        bifunction: (a: A, b: B, c: C) -> R
    ): List<Any?> {
        var value1: A? = defValue1
        var value2: B? = defValue2
        var value3: C? = defValue3
        val update = {
            val v1 = value1
            val v2 = value2
            val v3 = value3
            if (v1 != null && v2 != null && v3 != null) {
                dst.setValue(bifunction.invoke(v1, v2, v3))
            }
        }
        val job1 = src1.collectFlow(scope) { it1 ->
            if (it1 == value1) return@collectFlow
            value1 = it1
            update.invoke()
        }
        val job2 = src2.collectFlow(scope) { it2 ->
            if (it2 == value2) return@collectFlow
            value2 = it2
            update.invoke()
        }
        val job3 = src3.collectFlow(scope) { it3 ->
            if (it3 == value3) return@collectFlow
            value3 = it3
            update.invoke()
        }
        return listOf(job1, job2, job3)
    }

    fun <A, B, C, D, R> combineFlows(
        scope: Any?,
        src1: IStateFlowCompat<A>, defValue1: A? = null,
        src2: IStateFlowCompat<B>, defValue2: B? = null,
        src3: IStateFlowCompat<C>, defValue3: C? = null,
        src4: IStateFlowCompat<D>, defValue4: D? = null,
        dst: MutableStateFlowCompat<R>,
        bifunction: (a: A, b: B, c: C, d: D) -> R
    ): List<Any?> {
        var value1: A? = defValue1
        var value2: B? = defValue2
        var value3: C? = defValue3
        var value4: D? = defValue4
        val update = {
            val v1 = value1
            val v2 = value2
            val v3 = value3
            val v4 = value4
            if (v1 != null && v2 != null && v3 != null && v4 != null) {
                dst.setValue(bifunction.invoke(v1, v2, v3, v4))
            }
        }
        val job1 = src1.collectFlow(scope) { it1 ->
            if (it1 == value1) return@collectFlow
            value1 = it1
            update.invoke()
        }
        val job2 = src2.collectFlow(scope) { it2 ->
            if (it2 == value2) return@collectFlow
            value2 = it2
            update.invoke()
        }
        val job3 = src3.collectFlow(scope) { it3 ->
            if (it3 == value3) return@collectFlow
            value3 = it3
            update.invoke()
        }
        val job4 = src4.collectFlow(scope) { it4 ->
            if (it4 == value4) return@collectFlow
            value4 = it4
            update.invoke()
        }
        return listOf(job1, job2, job3, job4)
    }

    fun <A, B, C, D, E, R> combineFlows(
        scope: Any?,
        src1: IStateFlowCompat<A>, defValue1: A? = null,
        src2: IStateFlowCompat<B>, defValue2: B? = null,
        src3: IStateFlowCompat<C>, defValue3: C? = null,
        src4: IStateFlowCompat<D>, defValue4: D? = null,
        src5: IStateFlowCompat<E>, defValue5: E? = null,
        dst: MutableStateFlowCompat<R>,
        transform: (a: A, b: B, c: C, d: D, e: E) -> R
    ): List<Any?> {
        var value1: A? = defValue1
        var value2: B? = defValue2
        var value3: C? = defValue3
        var value4: D? = defValue4
        var value5: E? = defValue5
        val lock = Any()
        val update = {
            synchronized(lock) {
                val v1 = value1
                val v2 = value2
                val v3 = value3
                val v4 = value4
                val v5 = value5
                if (v1 != null && v2 != null && v3 != null && v4 != null && v5 != null) {
                    dst.setValue(transform.invoke(v1, v2, v3, v4, v5))
                }
            }
        }
        val job1 = src1.collectFlow(scope) { it1 ->
            synchronized(lock) {
                value1 = it1
                update.invoke()
            }
        }
        val job2 = src2.collectFlow(scope) { it2 ->
            synchronized(lock) {
                value2 = it2
                update.invoke()
            }
        }
        val job3 = src3.collectFlow(scope) { it3 ->
            synchronized(lock) {
                value3 = it3
                update.invoke()
            }
        }
        val job4 = src4.collectFlow(scope) { it4 ->
            synchronized(lock) {
                value4 = it4
                update.invoke()
            }
        }
        val job5 = src5.collectFlow(scope) { it5 ->
            synchronized(lock) {
                value5 = it5
                update.invoke()
            }
        }
        return listOf(job1, job2, job3, job4, job5)
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