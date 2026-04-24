package dev.lackluster.mihelper.hook.utils

import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.FieldResolver
import com.highcapable.kavaref.resolver.MethodResolver
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

internal class TypedField<T : Any, V>(private val rawField: Field) {
    init {
        rawField.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    fun get(instance: T?): V? = try {
        rawField.get(instance) as? V
    } catch (_: Throwable) { null }

    fun set(instance: T?, value: V?) {
        try { rawField.set(instance, value) } catch (_: Throwable) {}
    }
}

internal class TypedMethod<T : Any, R>(private val rawMethod: Method) {
    init {
        rawMethod.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    fun invoke(instance: T?, vararg args: Any?): R? = try {
        rawMethod.invoke(instance, *args) as? R
    } catch (_: Throwable) {
        null
    }
}

internal class TypedConstructor<T : Any>(private val rawConstructor: Constructor<*>) {
    init {
        rawConstructor.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    fun newInstance(vararg args: Any?): T? = try {
        rawConstructor.newInstance(*args) as? T
    } catch (_: Throwable) { null }
}

internal inline fun <reified V> Field.toTyped(): TypedField<Any, V> {
    return TypedField(this)
}

internal inline fun <reified R> Method.toTyped(): TypedMethod<Any, R> {
    return TypedMethod(this)
}

internal inline fun <reified V> FieldResolver<*>.toTyped(): TypedField<Any, V> {
    return TypedField(this.self)
}

internal inline fun <reified R> MethodResolver<*>.toTyped(): TypedMethod<Any, R> {
    return TypedMethod(this.self)
}

internal fun <T : Any> ConstructorResolver<T>.toTyped(): TypedConstructor<T> {
    return TypedConstructor(this.self)
}

internal inline fun <T : Any, reified V> FieldResolver<T>.getValueFrom(instance: T?): V? {
    return this.copy().of(instance).get() as? V
}

internal fun <T : Any> FieldResolver<T>.setValueTo(instance: T?, value: Any?) {
    this.copy().of(instance).set(value)
}