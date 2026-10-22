package io.github.deopping.bamboo.reflection.api

/**
 * Provides prepared access to a constructor.
 *
 * Supports creating instances without repeatedly performing reflective
 * constructor looking and access preparation.
 *
 * @param T the type of object created by the constructor
 * @author DeOpping
 * @since 0.1.0
 */
interface ConstructorAccessor<T : Any> {

    /**
     * Creates a new instance using the prepared constructor.
     *
     * @param arguments the arguments to pass to the constructor
     * @return the newly created instance
     * @throws IllegalArgumentException if the supplied arguments are incompatible
     * with the constructor's parameter types
     * @throws Throwable if the constructor throws an exception or the
     * constructor cannot be invoked
     * @since 0.1.0
     */
    fun newInstance(vararg arguments: Any?): T

}