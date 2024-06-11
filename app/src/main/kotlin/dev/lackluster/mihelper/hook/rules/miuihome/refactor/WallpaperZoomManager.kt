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

package dev.lackluster.mihelper.hook.rules.miuihome.refactor

import android.app.WallpaperManager
import android.content.Context
import android.os.IBinder
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import dev.lackluster.mihelper.utils.Math
import java.lang.reflect.Method
import java.util.concurrent.Executor

class WallpaperZoomManager(
    context: Context,
    private val windowToken: IBinder,
    private val setWallpaperZoomOut: Method,
    private val wallPaperExecutor: Executor
) {
    private var currentTargetRatio = 0.0f
    private var currentVelocity = 0.0f
    private var floatValueHolder = FloatValueHolder(currentTargetRatio)

    private val wallpaperManager : WallpaperManager
    private var mainAnimator: SpringAnimation = SpringAnimation(floatValueHolder)
    private var animCurrentRatio = 0.0f
    private var allowRestoreDirectly = false

    private val zoomInSpringForce = SpringForce().setStiffness(32.63f).setDampingRatio(1.0f)
    private val zoomOutSpringForce = SpringForce().setStiffness(100.00f).setDampingRatio(1.0f)
    init {
        wallpaperManager = context.getSystemService(WallpaperManager::class.java)
        mainAnimator.addUpdateListener { _, value, velocity ->
            currentVelocity = velocity
            zoomToDirectly(value)
        }
        mainAnimator.addEndListener { _, _, _, _ ->
            currentVelocity = 0.0f
        }
        mainAnimator.minimumVisibleChange = 0.01f
    }

    fun zoom(useAnim: Boolean, targetRatio: Float) {
        zoomTo(targetRatio, useAnim)
    }

    fun restore(directly: Boolean = false) {
        if (!directly) {
            zoomTo(animCurrentRatio, false)
        }
        else if (allowRestoreDirectly) {
            allowRestoreDirectly = false
            zoomToDirectly(animCurrentRatio)
        }
    }

    private fun zoomTo(ratio: Float, useAnim: Boolean) {
        val targetRatio = ratio.coerceIn(0.0f, 1.0f)
        if (mainAnimator.isRunning) {
            mainAnimator.cancel()
        }
        if (!useAnim || animCurrentRatio == targetRatio) {
            zoomToDirectly(targetRatio)
        }
        else {
            mainAnimator.setStartVelocity(-currentVelocity)
            if (targetRatio == 1.0f) {
                zoomInSpringForce.setFinalPosition(targetRatio)
                mainAnimator.spring = zoomInSpringForce
            }
            else {
                zoomOutSpringForce.setFinalPosition(targetRatio)
                mainAnimator.spring = zoomOutSpringForce
            }
            mainAnimator.animateToFinalPosition(targetRatio)
        }
    }

    private fun zoomToDirectly(ratio: Float) {
        wallPaperExecutor.execute {
            val zoomRatio = Math.linearInterpolate(1.0f, 0.6f, ratio)
            setWallpaperZoomOut.invoke(wallpaperManager, windowToken, zoomRatio)
            animCurrentRatio = ratio
            allowRestoreDirectly = true
        }
    }
}