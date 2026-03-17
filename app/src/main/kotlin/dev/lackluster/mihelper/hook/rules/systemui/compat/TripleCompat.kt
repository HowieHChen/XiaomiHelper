package dev.lackluster.mihelper.hook.rules.systemui.compat

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.makeAccessible
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzTriple

object TripleCompat {
    private val ctorTriple by lazy {
        clzTriple.resolve().firstConstructor {
            parameterCount = 2
        }.self.apply { makeAccessible() }
    }
    private val fldFirst by lazy {
        clzTriple.resolve().firstField {
            name = "first"
        }.self.apply { makeAccessible() }
    }
    private val fldSecond by lazy {
        clzTriple.resolve().firstField {
            name = "second"
        }.self.apply { makeAccessible() }
    }
    private val fldThird by lazy {
        clzTriple.resolve().firstField {
            name = "third"
        }.self.apply { makeAccessible() }
    }
    private val metGetFirst by lazy {
        clzTriple.resolve().firstMethod {
            name = "getFirst"
        }.self.apply { makeAccessible() }
    }
    private val metGetSecond by lazy {
        clzTriple.resolve().firstMethod {
            name = "getSecond"
        }.self.apply { makeAccessible() }
    }
    private val metGetThird by lazy {
        clzTriple.resolve().firstMethod {
            name = "getThird"
        }.self.apply { makeAccessible() }
    }

    fun create(obj1: Any, obj2: Any, obj3: Any): Any {
        return ctorTriple.newInstance(obj1, obj2, obj3)
    }

    fun getFirst(triple: Any): Any? {
        return metGetFirst.invoke(triple)
    }

    fun getSecond(triple: Any): Any? {
        return metGetSecond.invoke(triple)
    }

    fun getThird(triple: Any): Any? {
        return metGetThird.invoke(triple)
    }

    fun setFirst(triple: Any, value: Any?) {
        fldFirst.set(triple, value)
    }

    fun setSecond(triple: Any, value: Any?) {
        fldSecond.set(triple, value)
    }

    fun setThird(triple: Any, value: Any?) {
        fldThird.set(triple, value)
    }
}