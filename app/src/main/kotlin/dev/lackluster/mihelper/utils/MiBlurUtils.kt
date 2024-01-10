package dev.lackluster.mihelper.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.View

@SuppressLint("PrivateApi")
object MiBlurUtils {
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

    fun setViewBlur(view: View, radius: Int) {
        setMiViewBlurMode.invoke(view, radius)
    }

    fun setViewBackgroundBlur(view: View, radius: Int) {
        setMiBackgroundBlurMode.invoke(view, radius)
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

    fun supportBackgroundBlur() : Boolean {
        return isBackgroundBlurSupported
    }
}