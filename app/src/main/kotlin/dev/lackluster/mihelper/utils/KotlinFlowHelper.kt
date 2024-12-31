package dev.lackluster.mihelper.utils

import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import dev.lackluster.mihelper.hook.rules.shared.UnlimitedCropping.toClass

@Suppress("FunctionName")
object KotlinFlowHelper {
    private const val STATE_FLOW = "kotlinx.coroutines.flow.StateFlow"
    private const val STATE_FLOW_KT = "kotlinx.coroutines.flow.StateFlowKt"
    private const val READONLY_STATE_FLOW = "kotlinx.coroutines.flow.ReadonlyStateFlow"
    private val constructorMutableStateFlow by lazy {
        STATE_FLOW_KT.toClass().method {
            name = "MutableStateFlow"
            paramCount = 1
            modifiers { isStatic }
        }.get()
    }
    private val constructorReadonlyStateFlow by lazy {
        READONLY_STATE_FLOW.toClass().constructor {
            param(STATE_FLOW)
        }.get()
    }

    fun PackageParam.MutableStateFlow(initValue: Any?): Any? {
        return constructorMutableStateFlow.call(initValue)
    }

    fun PackageParam.ReadonlyStateFlow(initValue: Any?): Any? {
        return constructorReadonlyStateFlow.call(MutableStateFlow(initValue))
    }
}