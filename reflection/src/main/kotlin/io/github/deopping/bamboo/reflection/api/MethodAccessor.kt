package io.github.deopping.bamboo.reflection.api

/**
 * Provides prepared access to a method.
 *
 * Supports invoking both instance and static methods.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface MethodAccessor {

    /**
     * Invokes an instance method on the supplied target.
     *
     * @param instance the target instance
     * @param arguments the method arguments
     * @return the method result, or `null` if it returns `void`
     * @throws IllegalArgumentException if this accessor represents a static method
     * @see invokeStatic
     * @since 0.1.0
     */
    fun invoke(instance: Any, vararg arguments: Any?): Any?

    /**
     * Invokes a static method without requiring a target instance.
     *
     * @param arguments the method arguments
     * @return the method results, or `null` if it returns `void`
     * @throws IllegalArgumentException if this accessor represents an instance method
     * @see invoke
     * @since 0.1.0
     */
    fun invokeStatic(vararg arguments: Any?): Any?

}