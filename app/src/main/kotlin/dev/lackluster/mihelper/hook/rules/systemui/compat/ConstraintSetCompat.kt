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
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.toTyped

internal object ConstraintSetCompat : StaticHooker() {
    private val clzConstraintSet by "androidx.constraintlayout.widget.ConstraintSet".lazyClass()

    val ctorConstraintSet by lazy {
        clzConstraintSet.resolve().firstConstructor {
            parameterCount = 0
        }.toTyped()
    }
    val clear by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "clear"
            parameterCount = 2
            parameters(Int::class, Int::class)
        }?.toTyped<Unit>()
    }
    val setVisibility by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "setVisibility"
            parameterCount = 2
            parameters(Int::class, Int::class)
        }?.toTyped<Unit>()
    }
    val connect by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "connect"
            parameterCount = 4
            parameters(Int::class, Int::class, Int::class, Int::class)
        }?.toTyped<Unit>()
    }
    val setMargin by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "setMargin"
            parameterCount = 3
            parameters(Int::class, Int::class, Int::class)
        }?.toTyped<Unit>()
    }
    val setGoneMargin by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "setGoneMargin"
            parameterCount = 3
            parameters(Int::class, Int::class, Int::class)
        }?.toTyped<Unit>()
    }
    val applyTo by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "applyTo"
            parameterCount = 1
        }?.toTyped<Unit>()
    }
    val clone by lazy {
        clzConstraintSet.resolve().firstMethodOrNull {
            name = "clone"
            parameterCount = 1
            parameters("androidx.constraintlayout.widget.ConstraintLayout")
        }?.toTyped<Unit>()
    }

    override fun onInit() {
        clzConstraintSet
        ctorConstraintSet
        clear
        setVisibility
        connect
        setMargin
        setGoneMargin
        applyTo
        clone
    }
}