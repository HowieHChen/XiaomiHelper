package dev.lackluster.mihelper.hook.utils

import android.content.Context

fun Int.asString(context: Context, vararg formatArgs: Any?): String = context.getString(this, *formatArgs)
fun Int.asDimen(context: Context): Float = context.resources.getDimension(this)
fun Int.asColor(context: Context): Int = context.getColor(this)