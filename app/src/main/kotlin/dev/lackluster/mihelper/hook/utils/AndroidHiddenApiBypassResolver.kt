package dev.lackluster.mihelper.hook.utils

import com.highcapable.kavaref.resolver.processor.MemberProcessor
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class AndroidHiddenApiBypassResolver private constructor() : MemberProcessor.Resolver() {

    companion object {

        private val self by lazy { AndroidHiddenApiBypassResolver() }

        fun get() = self
    }

    override fun <T : Any> getDeclaredConstructors(declaringClass: Class<T>): List<Constructor<T>> {
        val constructors = HiddenApiBypass.getDeclaredMethods(declaringClass)
            .filterIsInstance<Constructor<T>>()
            .toList()
        return constructors
    }

    override fun <T : Any> getDeclaredMethods(declaringClass: Class<T>): List<Method> {
        val methods = HiddenApiBypass.getDeclaredMethods(declaringClass)
            .filterIsInstance<Method>()
            .toList()
        return methods
    }
}