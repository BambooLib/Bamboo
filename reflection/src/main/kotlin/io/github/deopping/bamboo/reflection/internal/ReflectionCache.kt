package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
import io.github.deopping.bamboo.reflection.api.FieldAccessor
import io.github.deopping.bamboo.reflection.api.MethodAccessor
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

internal class ReflectionCache {
    @Volatile
    private var classCaches: ClassValue<ClassAccessorCaches> = createClassCaches()

    internal fun <T : Any> getOrCreateConstructorAccessor(
        constructor: Constructor<T>,
        factory: ((ConstructorAccessor<T>) -> Boolean) -> ConstructorAccessor<T>,
    ): ConstructorAccessor<T> {
        val constructorCache = classCaches.get(constructor.declaringClass).constructors

        @Suppress("UNCHECKED_CAST")
        return constructorCache.computeIfAbsent(constructor) {
            factory { accessor ->
                constructorCache.remove(constructor, accessor)
            }
        } as ConstructorAccessor<T>
    }

    internal fun getOrCreateMethodAccessor(
        method: Method,
        factory: ((MethodAccessor) -> Boolean) -> MethodAccessor,
    ): MethodAccessor {
        val methodCache = classCaches.get(method.declaringClass).methods

        return methodCache.computeIfAbsent(method) {
            factory { accessor ->
                methodCache.remove(method, accessor)
            }
        }
    }

    internal fun getOrCreateFieldAccessor(
        field: Field,
        factory: ((FieldAccessor) -> Boolean) -> FieldAccessor,
    ): FieldAccessor {
        val fieldCache = classCaches.get(field.declaringClass).fields

        return fieldCache.computeIfAbsent(field) {
            factory { accessor ->
                fieldCache.remove(field, accessor)
            }
        }
    }

    internal fun clear() {
        classCaches = createClassCaches()
    }

    private fun createClassCaches(): ClassValue<ClassAccessorCaches> =
        object : ClassValue<ClassAccessorCaches>() {
            public override fun computeValue(type: Class<*>): ClassAccessorCaches = ClassAccessorCaches()
        }

    private class ClassAccessorCaches {
        public val constructors: ConcurrentHashMap<Constructor<*>, ConstructorAccessor<*>> = ConcurrentHashMap()
        public val methods: ConcurrentHashMap<Method, MethodAccessor> = ConcurrentHashMap()
        public val fields: ConcurrentHashMap<Field, FieldAccessor> = ConcurrentHashMap()
    }
}
