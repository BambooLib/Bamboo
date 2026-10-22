package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.ReflectionApi
import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
import io.github.deopping.bamboo.reflection.api.FieldAccessor
import io.github.deopping.bamboo.reflection.api.MethodAccessor
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class ReflectionApiImpl : ReflectionApi {

    private val constructorAccessors = ConcurrentHashMap<Constructor<*>, ConstructorAccessor<*>>()
    private val methodAccessors = ConcurrentHashMap<Method, MethodAccessor>()
    private val fieldAccessors = ConcurrentHashMap<Field, FieldAccessor>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> createConstructorAccessor(
        constructor: Constructor<T>
    ): ConstructorAccessor<T> =
        constructorAccessors.computeIfAbsent(constructor) {
            val lookup = MethodHandles.privateLookupIn(
                constructor.declaringClass,
                MethodHandles.lookup()
            )

            ConstructorAccessorImpl(
                lookup.unreflectConstructor(constructor)
            )
        } as ConstructorAccessor<T>

    override fun createMethodAccessor(method: Method): MethodAccessor =
        methodAccessors.computeIfAbsent(method) {
            val lookup = MethodHandles.privateLookupIn(
                method.declaringClass,
                MethodHandles.lookup()
            )

            MethodAccessorImpl(
                handle = lookup.unreflect(method),
                isStatic = Modifier.isStatic(method.modifiers)
            )
        }

    override fun createFieldAccessor(field: Field): FieldAccessor =
        fieldAccessors.computeIfAbsent(field) {
            val lookup = MethodHandles.privateLookupIn(
                field.declaringClass,
                MethodHandles.lookup()
            )

            val getter = lookup.unreflectGetter(field)
            val setter = if (Modifier.isFinal(field.modifiers)) {
                null
            } else {
                lookup.unreflectSetter(field)
            }

            FieldAccessorImpl(
                field = field,
                getter = getter,
                setter = setter
            )
        }

    override fun clearConstructorAccessorCache() {
        constructorAccessors.clear()
    }

    override fun clearMethodAccessorCache() {
        methodAccessors.clear()
    }

    override fun clearFieldAccessorCache() {
        fieldAccessors.clear()
    }

    override fun clearAccessorCaches() {
        clearConstructorAccessorCache()
        clearMethodAccessorCache()
        clearFieldAccessorCache()
    }

    override fun removeCachedConstructorAccessor(constructor: Constructor<*>): Boolean =
        constructorAccessors.remove(constructor) != null

    override fun removeCachedMethodAccessor(method: Method): Boolean =
        methodAccessors.remove(method) != null

    override fun removeCachedFieldAccessor(field: Field): Boolean =
        fieldAccessors.remove(field) != null

}