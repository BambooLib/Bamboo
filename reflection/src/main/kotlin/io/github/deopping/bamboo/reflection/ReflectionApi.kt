package io.github.deopping.bamboo.reflection

import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
import io.github.deopping.bamboo.reflection.api.FieldAccessor
import io.github.deopping.bamboo.reflection.api.MethodAccessor
import io.github.deopping.bamboo.reflection.internal.ReflectionApiImpl
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Entry point for Bamboo's reflection module.
 *
 * Provides utilities for preparing reflective access to Java methods.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ReflectionApi {

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
     * @since 0.1.0
     */
    fun <T : Any> createConstructorAccessor(
        constructor: Constructor<T>
    ): ConstructorAccessor<T>

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
     * @since 0.1.0
     */
    fun createMethodAccessor(method: Method): MethodAccessor

    /**
     * Creates an accessor for the specified field.
     *
     * The accessor prepares the field for subsequent read and write operations.
     * Access to private fields may depend on Java module access rules.
     * Final fields can be read but cannot be written through this accessor.
     *
     * @param field the field to prepare for access
     * @return an accessor for the specified field
     * @throws IllegalAccessException if the field cannot be accessed
     * through the required method handle lookup
     * @since 0.1.0
     */
    fun createFieldAccessor(field: Field): FieldAccessor

    /**
     * Clears all cached constructor accessors.
     *
     * Existing accessor instances remain usable as long as they are referenced.
     * Clearing the cache only removes the API's references to cached accessors;
     * it does not invalidate accessors already returned to callers.
     *
     * If another thread creates an accessor concurrently with this operation,
     * that accessor may be added to the cache again.
     *
     * @see clearAccessorCaches
     * @since 0.1.0
     */
    fun clearConstructorAccessorCache()

    /**
     * Clears all cached method accessors.
     *
     * Existing accessor instances remain usable as long as they are referenced.
     * Clearing the cache only removes the API's references to cached accessors;
     * it does not invalidate accessors already returned to callers.
     *
     * If another thread creates an accessor concurrently with this operation,
     * that accessor may be added to the cache again.
     *
     * @see clearAccessorCaches
     * @since 0.1.0
     */
    fun clearMethodAccessorCache()

    /**
     * Clears all cached field accessors.
     *
     * Existing accessor instances remain usable as long as they are referenced.
     * Clearing the cache only removes the API's references to cached accessors;
     * it does not invalidate accessors already returned to callers.
     *
     * If another thread creates an accessor concurrently with this operation,
     * that accessor may be added to the cache again.
     *
     * @see clearAccessorCaches
     * @since 0.1.0
     */
    fun clearFieldAccessorCache()

    /**
     * Clears all cached constructor, method, and field accessors.
     *
     * Existing accessor instances remain usable as long as they are referenced.
     * Clearing the caches only removes the API's references to cached accessors;
     * it does not invalidate accessors already returned to callers.
     *
     * If another thread creates an accessor concurrently with this operation,
     * that accessor may be added to the cache again.
     *
     * @see clearConstructorAccessorCache
     * @see clearMethodAccessorCache
     * @see clearFieldAccessorCache
     * @since 0.1.0
     */
    fun clearAccessorCaches()

    /**
     * Removes the cached accessor for the specified constructor.
     *
     * This operation does not invalidate accessor instances that have already
     * been returned to the callers.
     *
     * @param constructor the constructor whose cached accessor should be removed
     * @return `true` if a cached accessor was removed; `false` if no entry existed
     * @since 0.1.0
     */
    fun removeCachedConstructorAccessor(constructor: Constructor<*>): Boolean

    /**
     * Removes the cached accessor for the specified method.
     *
     * This operation does not invalidate accessor instances that have already
     * been returned to the callers.
     *
     * @param method the method whose cached accessor should be removed
     * @return `true` if a cached accessor was removed; `false` if no entry existed
     * @since 0.1.0
     */
    fun removeCachedMethodAccessor(method: Method): Boolean

    /**
     * Removes the cached accessor for the specified field.
     *
     * This operation does not invalidate accessor instances that have already
     * been returned to the callers.
     *
     * @param field the field whose cached accessor should be removed
     * @return `true` if a cached accessor was removed; `false` if no entry existed
     * @since 0.1.0
     */
    fun removeCachedFieldAccessor(field: Field): Boolean

    companion object {
        /**
         * Creates a new instance of the Reflection API
         *
         * @return a new Reflection API instance
         * @since 0.1.0
         */
        fun create(): ReflectionApi = ReflectionApiImpl()
    }

}