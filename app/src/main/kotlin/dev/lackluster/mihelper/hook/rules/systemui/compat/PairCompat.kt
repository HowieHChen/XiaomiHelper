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
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzPair

object PairCompat {
    private val ctorPair by lazy {
        clzPair.resolve().firstConstructor {
            parameterCount = 2
        }.self
    }
    private val fldFirst by lazy {
        clzPair.resolve().firstField {
            name = "first"
        }
    }
    private val fldSecond by lazy {
        clzPair.resolve().firstField {
            name = "second"
        }
    }
    private val metGetFirst by lazy {
        clzPair.resolve().firstMethod {
            name = "getFirst"
        }.self
    }
    private val metGetSecond by lazy {
        clzPair.resolve().firstMethod {
            name = "getSecond"
        }.self
    }

    fun create(obj1: Any, obj2: Any): Any {
        return ctorPair.newInstance(obj1, obj2)
    }

    fun getFirst(pair: Any): Any? {
        return metGetFirst.invoke(pair)
    }

    fun getSecond(pair: Any): Any? {
        return metGetSecond.invoke(pair)
    }

    fun setFirst(pair: Any, value: Any?) {
        fldFirst.copy().of(pair).set(value)
    }

    fun setSecond(pair: Any, value: Any?) {
        fldSecond.copy().of(pair).set(value)
    }
}