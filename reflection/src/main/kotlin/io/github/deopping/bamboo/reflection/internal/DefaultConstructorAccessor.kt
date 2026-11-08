package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType

/**
 * Default constructor accessor implementation.
 *
 * @param T the type created by the constructor
 * @author DeOpping
 * @since 1.0.0
 */
internal class DefaultConstructorAccessor<T : Any>(
    handle: MethodHandle,
    private val unregister: (ConstructorAccessor<T>) -> Boolean,
) : ConstructorAccessor<T> {
    private val invocationHandle: MethodHandle

    init {
        val parameterCount = handle.type().parameterCount()

        invocationHandle =
            handle
                .asSpreader(Array<Any?>::class.java, parameterCount)
                .asType(
                    MethodType.methodType(
                        Any::class.java,
                        Array<Any?>::class.java,
                    ),
                )
    }

    @Suppress("UNCHECKED_CAST")
    override fun newInstance(vararg arguments: Any?): T = invocationHandle.invokeExact(arguments) as T

    override fun unregister(): Boolean = unregister(this)
}
