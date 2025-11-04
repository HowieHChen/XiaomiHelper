package dev.lackluster.mihelper.hook.rules.systemui.compat

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzReadonlyStateFlow

class ReadonlyStateFlowCompat<T>() : IStateFlowCompat<T> {
    companion object {
        private val fldMutableStateFlow by lazy {
            clzReadonlyStateFlow?.resolve()?.firstFieldOrNull {
                type("kotlinx.coroutines.flow.MutableStateFlow")
            }?.self
        }
    }

    override var real: Any? = null

    fun of(readonlyStateFlow: Any?): ReadonlyStateFlowCompat<T> {
        real = readonlyStateFlow
        return this
    }

    fun getInternalMutableStateFlow(): Any? {
        return real?.let {
            fldMutableStateFlow?.get(it)
        }
    }

    fun getInternalMutableStateFlowCompat(): MutableStateFlowCompat<T>? {
        return MutableStateFlowCompat<T>().apply {
            of(getInternalMutableStateFlow())
        }
    }
}