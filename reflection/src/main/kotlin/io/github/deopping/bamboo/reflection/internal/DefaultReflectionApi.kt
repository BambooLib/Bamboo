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

internal class DefaultReflectionApi : ReflectionApi {
    private val cache: ReflectionCache = ReflectionCache()

    override fun <T : Any> createConstructorAccessor(constructor: Constructor<T>): ConstructorAccessor<T> =
        cache.getOrCreateConstructorAccessor(constructor) { unregister ->
            val lookup = createLookup(constructor.declaringClass)

            DefaultConstructorAccessor(
                handle = lookup.unreflectConstructor(constructor),
                unregister = unregister,
            )
        }

    override fun createMethodAccessor(method: Method): MethodAccessor =
        cache.getOrCreateMethodAccessor(method) { unregister ->
            val lookup = createLookup(method.declaringClass)

            DefaultMethodAccessor(
                handle = lookup.unreflect(method),
                isStatic = Modifier.isStatic(method.modifiers),
                unregister = unregister,
            )
        }

    override fun createFieldAccessor(field: Field): FieldAccessor =
        cache.getOrCreateFieldAccessor(field) { unregister ->
            val lookup = createLookup(field.declaringClass)

            DefaultFieldAccessor(
                field = field,
                getter = lookup.unreflectGetter(field),
                setter =
                    if (Modifier.isFinal(field.modifiers)) {
                        null
                    } else {
                        lookup.unreflectSetter(field)
                    },
                unregister = unregister,
            )
        }

    override fun clearAccessorCache() {
        cache.clear()
    }

    private fun createLookup(declaringClass: Class<*>): MethodHandles.Lookup =
        MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup())
}
