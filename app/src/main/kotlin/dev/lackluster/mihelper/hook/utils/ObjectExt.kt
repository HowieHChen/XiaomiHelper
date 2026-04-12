package dev.lackluster.mihelper.hook.utils

import java.util.WeakHashMap
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object ObjectExt {
    private const val SEGMENT_COUNT = 16
    private val segments = Array(SEGMENT_COUNT) { WeakHashMap<Any, MutableMap<String, Any?>>() }
    private val segmentLocks = Array(SEGMENT_COUNT) { Any() }

    private fun getSegmentIndex(obj: Any): Int {
        return (System.identityHashCode(obj) and 0x7FFFFFFF) % SEGMENT_COUNT
    }

    fun put(obj: Any, key: String, value: Any?) {
        val index = getSegmentIndex(obj)
        synchronized(segmentLocks[index]) {
            val map = segments[index].getOrPut(obj) { HashMap() }
            map[key] = value
        }
    }

    fun get(obj: Any, key: String): Any? {
        val index = getSegmentIndex(obj)
        synchronized(segmentLocks[index]) {
            return segments[index][obj]?.get(key)
        }
    }

    fun remove(obj: Any, key: String) {
        val index = getSegmentIndex(obj)
        synchronized(segmentLocks[index]) {
            segments[index][obj]?.let { map ->
                map.remove(key)
                if (map.isEmpty()) {
                    segments[index].remove(obj)
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> extraOf(key: String, default: T? = null) = object : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return (ObjectExt.get(thisRef, key) as? T) ?: default
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        ObjectExt.put(thisRef, key, value)
    }
}