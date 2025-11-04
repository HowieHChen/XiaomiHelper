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