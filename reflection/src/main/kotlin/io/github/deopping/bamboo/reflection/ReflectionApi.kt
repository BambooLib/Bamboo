package io.github.deopping.bamboo.reflection

import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
import io.github.deopping.bamboo.reflection.api.FieldAccessor
import io.github.deopping.bamboo.reflection.api.MethodAccessor
import io.github.deopping.bamboo.reflection.internal.DefaultReflectionApi
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Entry point for Bamboo's reflection module.
 *
 * Provides utilities for preparing reflective access to Java constructors,
 * methods, and fields.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ReflectionApi {
    /**
     * Creates an accessor for the specified constructor.
     *
     * The accessor prepares the constructor for subsequent instance creation.
     * Access to private constructors may depend on Java module access rules.
     *
     * @param constructor the constructor to prepare for invocation
     * @return an accessor for the specified constructor
     * @throws IllegalAccessException if the constructor cannot be accessed
     * through the required method handle lookup
     * @since 1.0.0
     */
    public fun <T : Any> createConstructorAccessor(constructor: Constructor<T>): ConstructorAccessor<T>

    /**
     * Creates an accessor for the specified method.
     *
     * The accessor prepares the method for subsequent invocation.
     * Access to private methods may depend on Java module access rules.
     *
     * @param method the method to prepare for invocation
     * @return an accessor for the specified method
     * @throws IllegalAccessException if the method cannot be accessed
     * through the required method handle lookup
     * @since 1.0.0
     */
    public fun createMethodAccessor(method: Method): MethodAccessor

    /**
     * Creates an accessor for the specified field.
     *
     * The accessor prepares the field for subsequent read and write operations.
     * Final fields can be read but cannot be written through this accessor.
     *
     * @param field the field to prepare for access
     * @return an accessor for the specified fields
     * @throws IllegalAccessException if the field cannot be accessed
     * through the required method handle lookup
     * @since 1.0.0
     */
    public fun createFieldAccessor(field: Field): FieldAccessor

    /**
     * Clears all cached constructor, method, and field accessors.
     *
     * Existing accessor instance remain usable as long as they are referenced.
     *
     * @since 1.0.0
     */
    public fun clearAccessorCache()

    /**
     * Factory methods for creating [ReflectionApi] instances.
     */
    public companion object {
        /**
         * Creates a new reflection API instance.
         *
         * @return a new reflection API instance
         * @since 1.0.0
         */
        public fun create(): ReflectionApi = DefaultReflectionApi()
    }
}
