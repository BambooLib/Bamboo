package io.github.deopping.bamboo.reflection.api

/**
 * Provides prepared access to a constructor.
 *
 * Supports creating instances without repeatedly performing reflective
 * constructor lookup and access preparation.
 *
 * @param T the type of object created by the constructor
 * @author DeOpping
 * @since 1.0.0
 */
public interface ConstructorAccessor<T : Any> : ReflectionAccessor {
    /**
     * Creates a new instance using the prepared constructor.
     *
     * @param arguments the arguments to pass to the constructor
     * @return the newly created instance
     * @throws IllegalArgumentException if the supplied arguments are incompatible
     * with the constructor's parameter types
     * @throws Throwable if the constructor throws an exception or cannot be invoked
     * @since 1.0.0
     */
    public fun newInstance(vararg arguments: Any?): T
}
