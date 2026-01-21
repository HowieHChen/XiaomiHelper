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
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzReadonlyStateFlow

class ReadonlyStateFlowCompat<T>() : IStateFlowCompat<T> {
    companion object {
        private val fldMutableStateFlow by lazy {
            clzReadonlyStateFlow?.resolve()?.firstFieldOrNull {
                type("kotlinx.coroutines.flow.MutableStateFlow")
            }?.self
        }
    }

    override var real: Any? = null

    fun of(readonlyStateFlow: Any?): ReadonlyStateFlowCompat<T> {
        real = readonlyStateFlow
        return this
    }

    fun getInternalMutableStateFlow(): Any? {
        return real?.let {
            fldMutableStateFlow?.get(it)
        }
    }

    fun getInternalMutableStateFlowCompat(): MutableStateFlowCompat<T> {
        return MutableStateFlowCompat<T>().apply {
            of(getInternalMutableStateFlow())
        }
    }
}