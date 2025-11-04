package dev.lackluster.mihelper.hook.rules.systemui.compat

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzMutableStateFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzReadonlyStateFlow
import dev.lackluster.mihelper.hook.rules.systemui.compat.CommonClassUtils.clzStateFlowKt

class MutableStateFlowCompat<T>() : IStateFlowCompat<T> {
    companion object {
        private val metMutableStateFlow by lazy {
            clzStateFlowKt?.resolve()?.firstMethodOrNull {
                name = "MutableStateFlow"
                parameterCount = 1
                modifiers(Modifiers.STATIC)
            }?.self
        }

        private val metSetValue by lazy {
            clzMutableStateFlow?.resolve()?.firstMethodOrNull {
                name = "setValue"
                parameterCount = 1
            }?.self
        }

        private val ctorReadonlyStateFlow by lazy {
            clzReadonlyStateFlow?.resolve()?.firstConstructorOrNull {
                parameterCount = 1
            }?.self
        }
    }

    override var real: Any? = null

    constructor(initValue: T?) : this() {
        real = metMutableStateFlow?.invoke(null, initValue)
    }

    fun of(mutableStateFlow: Any?): MutableStateFlowCompat<T> {
        real = mutableStateFlow
        return this
    }

    fun setValue(value: T?) {
        real?.let {
            metSetValue?.invoke(it, value)
        }
    }

    fun toReadonlyStateFlow(): Any? {
        return real?.let {
            ctorReadonlyStateFlow?.newInstance(it)
        }
    }

    fun toMutableStateFlow(): Any? {
        return real
    }
}