package dev.lackluster.mihelper.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.View

@SuppressLint("PrivateApi")
object MiBlurUtils {
    const val USAGE_UNKNOWN = 0
    const val USAGE_BACKGROUND = 1
    const val USAGE_FOREGROUND = 2

    const val USAGE_BIG_VIEW = 100
    const val USAGE_BUTTON = 101
    const val USAGE_DARK_EXCLUDE_TEXT = 104
    const val USAGE_DARK_FILTER = 102
    const val USAGE_FORCE_INVERT = 103
    const val USAGE_FORCE_TRANSPARENT = 105
    const val USAGE_INVERT_EXCLUDE_BITMAP = 106
    const val USAGE_PLACEHOLDER_FOR_IMAGEVIEW = 107

    private val setMiViewBlurMode by lazy {
        View::class.java.getDeclaredMethod("setMiViewBlurMode", Integer.TYPE)
    }
    private val setMiBackgroundBlurMode by lazy {
        View::class.java.getDeclaredMethod("setMiBackgroundBlurMode", Integer.TYPE)
    }
    private val setPassWindowBlurEnabled by lazy {
        View::class.java.getDeclaredMethod("setPassWindowBlurEnabled", java.lang.Boolean.TYPE)
    }
    private val setMiBackgroundBlurRadius by lazy {
        View::class.java.getDeclaredMethod("setMiBackgroundBlurRadius", Integer.TYPE)
    }
    private val addMiBackgroundBlendColor by lazy {
        View::class.java.getDeclaredMethod("addMiBackgroundBlendColor", Integer.TYPE, Integer.TYPE)
    }
    private val setMiBackgroundBlurScaleRatio by lazy {
        View::class.java.getDeclaredMethod("setMiBackgroundBlurScaleRatio", java.lang.Float.TYPE)
    }
    private val clearMiBackgroundBlendColor by lazy {
        View::class.java.getDeclaredMethod("clearMiBackgroundBlendColor")
    }
    private val isBackgroundBlurSupported by lazy {
        try {
            Class.forName("android.os.SystemProperties").getDeclaredMethod("getBoolean", String::class.java, java.lang.Boolean.TYPE)
                .invoke(null, "persist.sys.background_blur_supported", false) as Boolean
        }
        catch (e: Exception) {
            false
        }
    }
    fun clearAllBlur(view: View) {
        // resetBlurColor(view)
        setViewBackgroundBlur(view, 0)
        setViewBlur(view, 0)
        setBlurRadius(view, 0)
        setPassWindowBlurEnable(view, false)
    }

    fun setViewBlur(view: View, mode: Int) {
        setMiViewBlurMode.invoke(view, mode)
    }

    fun setViewBackgroundBlur(view: View, mode: Int) {
        setMiBackgroundBlurMode.invoke(view, mode)
    }

    fun setBlurRadius(view: View, radius: Int) {
        if (radius < 0 || radius > 200) {
            Log.e("MiBlurUtils", "setMiBackgroundBlurRadius error radius is " + radius + " " + view.javaClass.name + " hashcode " + view.hashCode())
            return
        }
        setMiBackgroundBlurRadius.invoke(view, radius)
    }

    fun setPassWindowBlurEnable(view: View, enabled: Boolean) {
        setPassWindowBlurEnabled.invoke(view, enabled)
    }

    fun setBlurColor(view: View, i1: Int, i2: Int) {
        addMiBackgroundBlendColor.invoke(view, i1, i2)
    }

    fun resetBlurColor(view: View) {
        clearMiBackgroundBlendColor.invoke(view)
    }

    fun setBackgroundBlurScaleRatio(view: View, ratio: Float) {
        setMiBackgroundBlurScaleRatio.invoke(view, ratio)
    }

    fun supportBackgroundBlur() : Boolean {
        return isBackgroundBlurSupported
    }
}