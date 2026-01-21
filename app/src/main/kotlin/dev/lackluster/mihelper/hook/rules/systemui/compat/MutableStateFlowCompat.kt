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
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMutableStateFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzReadonlyStateFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzStateFlowKt

class MutableStateFlowCompat<T>() : IStateFlowCompat<T> {
    companion object {
        private val metMutableStateFlow by lazy {
            clzStateFlowKt?.resolve()?.firstMethodOrNull {
                name = "MutableStateFlow"
                parameterCount = 1
                modifiers(Modifiers.STATIC)
            }?.self
        }

        private val metSetValue by lazy {
            clzMutableStateFlow?.resolve()?.firstMethodOrNull {
                name = "setValue"
                parameterCount = 1
            }?.self
        }

        private val ctorReadonlyStateFlow by lazy {
            clzReadonlyStateFlow?.resolve()?.firstConstructorOrNull {
                parameterCount = 1
            }?.self
        }
    }

    override var real: Any? = null

    constructor(initValue: T?) : this() {
        real = metMutableStateFlow?.invoke(null, initValue)
    }

    fun of(mutableStateFlow: Any?): MutableStateFlowCompat<T> {
        real = mutableStateFlow
        return this
    }

    fun setValue(value: T?) {
        real?.let {
            metSetValue?.invoke(it, value)
        }
    }

    fun toReadonlyStateFlow(): Any? {
        return real?.let {
            ctorReadonlyStateFlow?.newInstance(it)
        }
    }

    fun toMutableStateFlow(): Any? {
        return real
    }
}