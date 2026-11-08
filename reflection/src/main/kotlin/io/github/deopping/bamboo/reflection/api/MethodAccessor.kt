package io.github.deopping.bamboo.reflection.api

/**
 * Provides prepared access to a method.
 *
 * Supports invoking both instance and static methods.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface MethodAccessor : ReflectionAccessor {
    /**
     * Indicates whether this accessor represents a static method.
     *
     * @return `true` if the method is static
     * @since 1.0.0
     */
    public val isStatic: Boolean

    /**
     * Invokes an instance method on the supplied target.
     *
     * @param instance the target instance
     * @param arguments the method arguments
     * @return the method result, or `null` if it returns `void` or `null`
     * @throws IllegalArgumentException if this accessor represents a static method
     * @since 1.0.0
     */
    public fun invoke(
        instance: Any,
        vararg arguments: Any?,
    ): Any?

    /**
     * Invokes a static method without requiring a target instance.
     *
     * @param arguments the method arguments
     * @return the method result, or `null` if it returns `void` or `null`
     * @throws IllegalArgumentException if this accessor represents an instance method
     * @since 1.0.0
     */
    public fun invokeStatic(vararg arguments: Any?): Any?
}
