package dev.lackluster.mihelper.app.manager

import dev.lackluster.mihelper.utils.MLog
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class XposedServiceManager : XposedServiceHelper.OnServiceListener {
    private val _serviceFlow = MutableStateFlow<XposedService?>(null)
    val serviceFlow = _serviceFlow.asStateFlow()

    val currentService: XposedService?
        get() = _serviceFlow.value

    init {
        XposedServiceHelper.registerListener(this)
    }

    override fun onServiceBind(service: XposedService) {
        MLog.d {
            "XposedServiceManager.onServiceBind"
        }
        _serviceFlow.value = service
    }

    override fun onServiceDied(service: XposedService) {
        MLog.d {
            "XposedServiceManager.onServiceDied"
        }
        _serviceFlow.value = null
    }
}