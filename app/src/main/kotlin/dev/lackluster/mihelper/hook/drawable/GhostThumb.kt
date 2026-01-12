package dev.lackluster.mihelper.hook.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class GhostThumb(private val w: Int, private val h: Int) : Drawable() {
    override fun getIntrinsicWidth() = w
    override fun getIntrinsicHeight() = h
    override fun draw(canvas: Canvas) {}
    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
    @Deprecated("Deprecated in Java")
    override fun getOpacity() = PixelFormat.TRANSLUCENT
}